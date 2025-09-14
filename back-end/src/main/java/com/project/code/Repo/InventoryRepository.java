package com.project.code.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory by product ID and store ID
    Inventory findByProduct_IdAndStore_Id(Long productId, Long storeId);

    // Find inventory list by store ID
    List<Inventory> findByStore_Id(Long storeId);

    // Delete inventory by product ID
    @Modifying
    @Transactional
    void deleteByProduct_Id(Long productId);
}
