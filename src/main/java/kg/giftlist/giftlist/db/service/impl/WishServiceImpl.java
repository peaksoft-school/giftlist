package kg.giftlist.giftlist.db.service.impl;
import kg.giftlist.giftlist.db.models.*;
import kg.giftlist.giftlist.db.repositories.*;
import kg.giftlist.giftlist.dto.SimpleResponse;
import kg.giftlist.giftlist.dto.mapper.wish.WishEditMapper;
import kg.giftlist.giftlist.dto.mapper.wish.WishViewMapper;
import kg.giftlist.giftlist.dto.wish.WishRequest;
import kg.giftlist.giftlist.dto.wish.WishResponse;
import kg.giftlist.giftlist.exception.NotFoundException;
import kg.giftlist.giftlist.exception.WishNotFoundException;
import kg.giftlist.giftlist.db.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.ForbiddenException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {

    private final ComplaintRepository complaintRepository;
    private final WishRepository wishRepository;
    private final WishEditMapper editMapper;
    private final WishViewMapper viewMapper;
    private final UserRepository userRepository;
    private final HolidayRepository holidayRepository;

    @Override
    @Transactional
    public WishResponse create(WishRequest wishRequest) {
        User user = getAuthenticatedUser();
        Wish wish = editMapper.create(wishRequest);
        if (wishRequest.getPhoto()==null){
            wish.setWishPhoto("https://giftlist-bucket.s3.amazonaws.com/1661860549270wishes-default-image.png");
        }else {
            wish.setWishPhoto(wishRequest.getPhoto());
        }
        wish.setUser(user);
        user.setWishes(List.of(wish));
        Holiday holiday = holidayRepository.findById(wishRequest.getHolidayId()).orElseThrow(()
                -> new NotFoundException("Holiday not found"));
        wish.setCreatedAt(LocalDateTime.now());
        if (user.getHolidays().contains(holiday)){
            wish.setHoliday(holiday);
        }else {
            log.error("Holiday not found");
            throw new NotFoundException("Holiday not found");
        }
        log.info("Wish with id: {} successfully saved in db", wish.getId());
        return viewMapper.viewCommonWishCard(user,wish);
    }

    @Transactional
    @Override
    public WishResponse update(Long id, WishRequest wishRequest) {
        User user = getAuthenticatedUser();
        Wish wish = wishRepository.findById(id).orElseThrow(() -> new WishNotFoundException("Wish with id " + id + " not found!"));
        Holiday holiday = holidayRepository.findById(wishRequest.getHolidayId()).orElseThrow(()
                -> new NotFoundException("Holiday not found"));
        if (user.getHolidays().contains(holiday)){
            wish.setHoliday(holiday);
        }else {
            log.error("Holiday not found");
            throw new NotFoundException("Holiday not found");
        }
        if (wish.getUser() == user) {
            editMapper.update(wish, wishRequest);
        }
        log.info("Wish with id: {} successfully updated in db", wish.getId());
        return viewMapper.viewCommonWishCard(user, wish);
    }

    @Override
    public WishResponse findById(Long id) {
        Wish wish = getWishById(id);
        User user = wish.getUser();
        return viewMapper.viewCommonWishCard(user,wish);
    }

    private Wish getWishById(Long wishId) {
        return wishRepository.findById(wishId).
                orElseThrow(() -> new WishNotFoundException("Wish with id = " + wishId + " not found!"));
    }

    @Transactional
    public SimpleResponse deleteById(Long id) {
        Wish wish = wishRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Wish with id = " + id + " not found!"));
        if (wish.getBooking()!=null) {
            User user = wish.getBooking().getUser();
            user.getBooking().getWishes().remove(wish);
            wish.setBooking(null);
        }
        List<Complaint> complaints = complaintRepository.findAll();
        complaints.removeIf(c -> Objects.equals(wish.getComplaints(), c));
        wishRepository.deleteById(id);
        log.info("Wish with id: {} successfully deleted from db", id);
        return new SimpleResponse("Deleted!", "Wish successfully deleted!");
    }

    public List<WishResponse> getAllWishes() {
        User user = getAuthenticatedUser();
        return viewMapper.getAllWishes(wishRepository.getAllUserWishes(user.getId()));
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(() ->
                new ForbiddenException("User not found!"));
    }

    @Transactional
    public WishResponse addToMyWish(Long wishId) {
        User user = getAuthenticatedUser();
        Wish friendWish = getWishById(wishId);
        friendWish.setIsAddToMyWish(true);
        Wish newWish = new Wish();
        newWish.setWishName(friendWish.getWishName());
        newWish.setWishLink(friendWish.getWishLink());
        newWish.setWishPhoto(friendWish.getWishPhoto());
        newWish.setWishDate(LocalDate.now());
        newWish.setCreatedAt(LocalDateTime.now());
        newWish.setDescription(friendWish.getDescription());
        Holiday holiday = new Holiday();
        holiday.setPhoto(friendWish.getHoliday().getPhoto());
        holiday.setName(friendWish.getHoliday().getName());
        holiday.setHolidayDate(friendWish.getHoliday().getHolidayDate());
        holiday.setUser(user);
        holidayRepository.save(holiday);
        newWish.setHoliday(holiday);
        newWish.setUser(user);
        user.setWishes(List.of(newWish));
        log.info("Wish with id: {} successfully saved in db", newWish.getId());
        return viewMapper.viewCommonWishCard(user,newWish);
    }
}
