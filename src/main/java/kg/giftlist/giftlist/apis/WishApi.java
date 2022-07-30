package kg.giftlist.giftlist.apis;

import kg.giftlist.giftlist.models.Wish;
import kg.giftlist.giftlist.services.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/wish")
public class WishApi {

    private final WishService wishService;

    @GetMapping("/search/{keyword}")
    List<Wish> search(@PathVariable String keyword){
        return wishService.searchWithGiftName(keyword);
    }
}
