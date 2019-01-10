package com.leyou.order.service;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyo.auth.entity.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/7 9:56
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PayHelper payHelper;

    /**
     * 创建订单
     *
     * @param orderDTO
     * @return
     */
    @Transactional
    public Long dcreateOrder(OrderDTO orderDTO) {
        // 1、新增订单
        Order order = new Order();

        // 1.1 写入订单编号，填写基本信息
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        // 1.2 用户信息
        UserInfo userInfo = UserInterceptor.getUser();
        order.setUserId(userInfo.getId());
        order.setBuyerNick(userInfo.getUsername());
        order.setBuyerRate(false);

        // 1.3 收货人地址
        AddressDTO addressDTO = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addressDTO.getName());
        order.setReceiverAddress(addressDTO.getAddress());
        order.setReceiverCity(addressDTO.getCity());
        order.setReceiverDistrict(addressDTO.getDistrict());
        order.setReceiverMobile(addressDTO.getPhone());
        order.setReceiverState(addressDTO.getState());
        order.setReceiverZip(addressDTO.getZipCode());

        // 1.4 金额
        @NotNull List<CartDTO> cartDTOList = orderDTO.getCarts();
        Map<Long, Integer> skuMap = cartDTOList.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        Set<Long> ids = skuMap.keySet();
        // 在集合构造器里面传入一个集合，把set转成list
        List<Sku> skus = goodsClient.querySkusBySpuIds(new ArrayList<>(ids));
        Long totalPrice = 0L;

        // 准备orderDetail集合
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (Sku sku : skus) {
            Integer num = skuMap.get(sku.getId());
            totalPrice = sku.getPrice() * num; // 购买商品的数量

            // 填写订单详情属性
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setSkuId(sku.getId());

            // 获取商品的第一张图片
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            orderDetail.setNum(num);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setTitle(sku.getTitle());
            orderDetailList.add(orderDetail);
        }
        // 总金额
        order.setTotalPay(totalPrice);

        // 实付金额 = 总金额 + 邮费 - 优惠(没做优惠)
        order.setActualPay(totalPrice + order.getPostFee() - 0);
        // 1.5 order写入数据库
        int count = orderMapper.insertSelective(order);//有选择的插入，因为数据库有默认值，没填的值我们不写入
        if(count != 1){
            log.error("[创建订单] 创建订单失败，orderId:{}", order.getOrderId());
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        // 2、新增订单详情
        count = orderDetailMapper.insertList(orderDetailList);
        // 批量新增，每次成功新增一条就会返回1，多少条新增成功就返回多少
        if(count != orderDetailList.size()){
            log.error("[创建订单] 创建订单详情失败，orderId:{}", order.getOrderId());
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        // 3、新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(order.getOrderId());
        // 获取订单枚举的状态码
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.getStatusCode());
        count = orderStatusMapper.insertSelective(orderStatus);
        if(count != 1){
            log.error("[创建订单] 创建订单状态失败，orderId:{}", order.getOrderId());
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        // 4、减少库存，远程调用商品微服务接口，完成减少商品库存
        @NotNull List<CartDTO> cartDTOS = orderDTO.getCarts();
        goodsClient.decreaseStock(cartDTOS);

        // 返回订单id
        return orderId;
    }

    /**
     * 根据订单id查询订单
     *
     * @param orderId
     * @return
     */
    public Order queryOrderById(Long orderId) {
        // 查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        // 查询订单详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
        if(CollectionUtils.isEmpty(orderDetails)){
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(orderDetails);

        // 查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if(orderStatus == null){
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    /**
     * 创建订单支付链接
     *
     * @param orderId
     * @return
     */
    public String createPayUrl(Long orderId) {
        // 查询订单ID获取订单对象
        Order order = queryOrderById(orderId);
        // 判断订单状态
        Integer status = order.getOrderStatus().getStatus();
        if(OrderStatusEnum.UN_PAY .getStatusCode() != status){
            // 订单状态异常
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }

        // 实际支付金额
        Long totalPay = /*order.getActualPay()*/ 1L;
        // 商品描述，把商品的标题作为商品的描述
        String desc = /*order.getOrderDetails().get(0).getTitle()*/ "暗氵愧(测试)";
        // 生成支付订单的url
        String url = payHelper.createOrder(orderId, totalPay, desc);
        if(StringUtils.isBlank(url)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        return url;
    }

    /**
     * 处理微信支付成功回调
     *
     * @param result
     */
    @Transactional
    public void handleNotify(Map<String, String> result) {
        // 1 数据校验
        payHelper.isSuccess(result);

        // 2 校验签名
        payHelper.isValidSign(result);

        // 3 校验订单金额是否一致
        String totalFeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");
        if(StringUtils.isBlank(totalFeeStr) || StringUtils.isBlank(tradeNo)){
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        // 获取结果中的金额
        Long totalFee = Long.valueOf(totalFeeStr);
        // 获取订单标号
        Long orderId = Long.valueOf(tradeNo);

        // 3.2 校验订单的金额与实付金额是否一致（这里我们把所有商品都设置成了1分钱...）
        if(totalFee != /*(totalFee)*/ 1L){
            // 金额不符，抛异常
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }

        // 4 修改订单状态
        // 4.1 根据订单id查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);

        // 4.2 校验订单状态
        if(OrderStatusEnum.UN_PAY.getStatusCode() != orderStatus.getStatus()){
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }

        // 4.3 设置订单属性
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.PAYED.getStatusCode());
        orderStatus.setPaymentTime(new Date());

        // 4.4 修改订单
        int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if(count != 1){
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }

        // 记录日志
        String payMoney = result.get("total_fee");
        payMoney = NumberUtils.format(payMoney);
        log.info("[支付成功] 订单支付成功！，订单编号：{}，订单金额：{}",
                orderId, payMoney + "元");
    }

    /**
     * 根据orderId查询订单状态
     *
     * @param orderId
     * @return
     */
    public PayState queryOrderState(Long orderId) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        // 判断是否支付
        if(orderStatus != null && orderStatus.getStatus() != OrderStatusEnum.UN_PAY.getStatusCode()){
            // 已支付，真的支付
            return PayState.SUCCESS;
        }
        // 如果未支付，但其实不一定是未支付，必须去微信支付查询状态
        PayState payState = payHelper.queryPayState(orderId);
        return payState;
    }
}
