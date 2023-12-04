package com.example.reservation.service;

import com.example.reservation.domain.StoreEntity;
import com.example.reservation.model.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class StoreServiceTest {
    @Autowired
    private StoreService storeService;

    @Test
    void updateStoreSuccess() {
        //given
        Store.updateStore store = Store.updateStore.builder()
                .managerName("유현우")
                .storeName("맘스터치")
                .location("전남 광양")
                .explanation("맛있습니다")
                .build();
        //when
        StoreEntity storeEntity = this.storeService.updateStore(store);

        //then
        assertEquals("맘스터치", storeEntity.getStoreName());
    }

}