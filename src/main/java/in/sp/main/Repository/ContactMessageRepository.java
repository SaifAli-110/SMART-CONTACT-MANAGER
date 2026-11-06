package in.sp.main.Repository;

import in.sp.main.Model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

//    you can implement Lot, of own method.
}
