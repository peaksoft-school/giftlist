package kg.giftlist.giftlist.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishes")
@NoArgsConstructor
@Getter
@Setter
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "wish_gen",sequenceName = "wish_seq", allocationSize = 1)
    private Long id;

    private String giftName;

    private String giftLink;

    private String giftPhoto;

    private LocalDateTime createdAt;

    private LocalDate wishDate;

    private String description;

    private Boolean isBlock;

    private Boolean isHidden;

    @ManyToOne
    private Booking booking;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "users")
    @NotNull(message = "User not set")
    @JsonIgnore
    private User user;

    @Column(name = "users", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    private User fromUser;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday")
    @JsonIgnore
    private Holiday holidays;

    @Column(name = "holiday", insertable = false, updatable = false)
    private String holidayName;


}
