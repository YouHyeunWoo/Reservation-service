package com.example.reservation.model;

import com.example.reservation.domain.StoreEntity;
import lombok.Builder;
import lombok.Data;

public class Store {

    @Data //점장이 가게를 등록할 때 사용할 클래스
    @Builder
    public static class updateStore {
        private String storeName;
        private String location;
        private String explanation;
        private String managerName;

        public StoreEntity toStore() {
            return StoreEntity.builder()
                    .managerName(this.managerName)
                    .storeName(this.storeName)
                    .location(this.location)
                    .explanation(this.explanation)
                    .build();
        }
    }

    //고객이 가게를 찾을때 리턴 클래스
    @Data
    @Builder
    public static class findStore {
        private String storeName;
        private String location;
        private String explanation;
    }

}
