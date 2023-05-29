package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    /**
     * PK가 GeneratedValue가 아닐 경우?
     * -> id가 null이 아니므로 persist가 아닌 merge로 감
     * -> merge로 갔다가 id가 없는 것을 확인하고 다시 persist
     * -> 성능 저하가 여기서 나타남
     * -> 이 부분을 조심해야함
     */
    @Test
    public void save() {
        Item item = new Item("A");
        itemRepository.save(item);
    }
}