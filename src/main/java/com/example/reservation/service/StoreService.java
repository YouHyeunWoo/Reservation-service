package com.example.reservation.service;

import com.example.reservation.domain.StoreEntity;
import com.example.reservation.exception.impl.AlreadyExistsStoreException;
import com.example.reservation.exception.impl.NoMatchManagerNameException;
import com.example.reservation.exception.impl.NoStorenameException;
import com.example.reservation.model.Store;
import com.example.reservation.repository.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    //매장 등록 메소드
    //매장이 존재 하는지 확인 >> 같은 매장 없으면 등록
    //매장 이름은 (맘스터치 부산점)과 같이 작성
    public StoreEntity updateStore(Store.updateStore store) {
        boolean exists = this.storeRepository.existsByStoreName(store.getStoreName());
        if (exists) {
            throw new AlreadyExistsStoreException();
        }

        return this.storeRepository.save(store.toStore());
    }

    //매장 수정 메소드
    //매장이 존재하는지, store테이블에 저장된 매장의 점장 이름과 입력된 이름이 같은지 확인
    //설명만 수정 가능, 다른 내용 수정은 매장을 새로 등록
    //같다면 덮어쓰기
    public StoreEntity modifyStore(Store.updateStore store) {
        StoreEntity storeEntity = getStoreEntity(store.getStoreName());

        if (!Objects.equals(storeEntity.getManagerName(), store.getManagerName())) {
            throw new NoMatchManagerNameException();
        }
        storeEntity.setExplanation(store.getExplanation());

        return this.storeRepository.save(storeEntity);
    }

    //매장 삭제 메소드
    //가게의 이름이 존재 하는지 확인
    //등록된 가게의 점장 이름과 입력한 내용의 이름이 다를 경우 예외처리
    public String deleteStore(String storeName, String managerName) {
        StoreEntity storeEntity = getStoreEntity(storeName);

        if (!Objects.equals(storeEntity.getManagerName(), managerName)) {
            throw new NoMatchManagerNameException();
        }

        this.storeRepository.deleteByStoreName(storeEntity.getStoreName());
        return storeEntity.getStoreName();
    }

    //매장 찾기
    //해당 매장 이름이 없으면 예외처리
    public Store.findStore findStore(String storeName) {
        StoreEntity storeEntity = getStoreEntity(storeName);

        return Store.findStore.builder()
                .storeName(storeEntity.getStoreName())
                .location(storeEntity.getLocation())
                .explanation(storeEntity.getExplanation())
                .build();
    }

    //storeEntity 찾는 메소드 추출(중복 코딩 제거)
    private StoreEntity getStoreEntity(String storeName) {
        return this.storeRepository.findByStoreName(storeName)
                .orElseThrow(NoStorenameException::new);
    }

}
