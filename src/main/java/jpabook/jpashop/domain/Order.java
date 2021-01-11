package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="ORDERS")
public class Order {

    @Id @GeneratedValue
    @Column(name="ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MEMBER_ID")
    private Member member;

    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    private Delivery delivery;

    private Date orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
