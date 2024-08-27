package edutrack.repository;

import edutrack.entity.students.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByStudentId(Long id);

	void deleteByStudentId(Long id);
}
