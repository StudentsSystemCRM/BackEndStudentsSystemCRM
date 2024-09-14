package edutrack.payment.repository;

import java.util.List;

import edutrack.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

	List<PaymentEntity> findByStudentId(Long id);

	void deleteByStudentId(Long id);
}
