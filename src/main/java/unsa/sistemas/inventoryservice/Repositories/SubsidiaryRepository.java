package unsa.sistemas.inventoryservice.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unsa.sistemas.inventoryservice.Models.Subsidiary;

@Repository
public interface SubsidiaryRepository extends JpaRepository<Subsidiary, Long> {
    Page<Subsidiary> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
