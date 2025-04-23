package livart.common.domain.user.repository;

import livart.common.domain.user.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("""
    SELECT a FROM Admin a
    WHERE LOWER(a.loginId) LIKE LOWER(CONCAT('%', :keyword, '%'))
    AND (:smsNotiEnabled IS NULL OR a.smsNotiEnabled = :smsNotiEnabled)
""")
    Page<Admin> findByLoginId(@Param("keyword") String keyword,
                              @Param("smsNotiEnabled") Boolean smsNotiEnabled,
                              Pageable pageable);

    @Query("""
    SELECT a FROM Admin a
    WHERE LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    AND (:smsNotiEnabled IS NULL OR a.smsNotiEnabled = :smsNotiEnabled)
""")
    Page<Admin> findByEmail(@Param("keyword") String keyword,
                            @Param("smsNotiEnabled") Boolean smsNotiEnabled,
                            Pageable pageable);

    @Query("""
    SELECT a FROM Admin a
    WHERE LOWER(a.adminName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    AND (:smsNotiEnabled IS NULL OR a.smsNotiEnabled = :smsNotiEnabled)
""")
    Page<Admin> findByAdminName(@Param("keyword") String keyword,
                                @Param("smsNotiEnabled") Boolean smsNotiEnabled,
                                Pageable pageable);

    @Query("""
    SELECT a FROM Admin a
    WHERE LOWER(a.phoneNum) LIKE LOWER(CONCAT('%', :keyword, '%'))
    AND (:smsNotiEnabled IS NULL OR a.smsNotiEnabled = :smsNotiEnabled)
""")
    Page<Admin> findByPhoneNum(@Param("keyword") String keyword,
                               @Param("smsNotiEnabled") Boolean smsNotiEnabled,
                               Pageable pageable);

    @Query("""
    SELECT a FROM Admin a
    WHERE LOWER(a.officeNum) LIKE LOWER(CONCAT('%', :keyword, '%'))
    AND (:smsNotiEnabled IS NULL OR a.smsNotiEnabled = :smsNotiEnabled)
""")
    Page<Admin> findByOfficeNum(@Param("keyword") String keyword,
                                @Param("smsNotiEnabled") Boolean smsNotiEnabled,
                                Pageable pageable);

    @Query("""
    SELECT a FROM Admin a
    WHERE (:noti IS NULL OR a.smsNotiEnabled = :noti)
""")
    Page<Admin> findAllWithNotiFilter(@Param("noti") Boolean noti, Pageable pageable);

}
