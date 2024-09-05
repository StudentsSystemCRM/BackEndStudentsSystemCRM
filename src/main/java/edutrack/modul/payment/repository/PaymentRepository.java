package edutrack.modul.payment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edutrack.modul.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByStudentId(Long id);

	void deleteByStudentId(Long id);
}
