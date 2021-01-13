package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {

        //Given
        Member member = createMember();
        int bookPrice = 10000;
        int stockQuantity = 10;
        Item item = createBook("시골 JPA", bookPrice, stockQuantity);
        int orderCount =2;

        //When
        Long orderId = orderService.order(member.getId(),
                item.getId(), orderCount);

        //Then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(bookPrice * orderCount);
        assertThat(item.getStockQuantity()).isEqualTo(stockQuantity-orderCount);
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //Given
        Member member = createMember();
        int bookPrice = 10000;
        int stockQuantity = 10;
        Item item = createBook("시골 JPA", bookPrice, stockQuantity);
        int orderCount = 11; //재고보다 많은 수량

        //When
        NotEnoughStockException e = assertThrows(NotEnoughStockException.class,
                ()->orderService.order(member.getId(), item.getId(), orderCount));

        //Then
        assertThat(e.getMessage()).isEqualTo("need more stock");
    }

    @Test
    public void 주문취소() {

        //Given
        Member member = createMember();
        int bookPrice = 10000;
        int stockQuantity = 10;
        Item item = createBook("시골 JPA", bookPrice, stockQuantity);
        int orderCount =2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //When
        orderService.cancelOrder(orderId);

        //Then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockQuantity()).isEqualTo(stockQuantity);
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}