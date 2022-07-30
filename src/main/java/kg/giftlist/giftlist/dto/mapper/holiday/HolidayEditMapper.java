package kg.giftlist.giftlist.dto.mapper.holiday;

import kg.giftlist.giftlist.dto.holiday.HolidayRequest;
import kg.giftlist.giftlist.models.Holiday;
import org.springframework.stereotype.Component;

@Component
public class HolidayEditMapper {

    public Holiday create(HolidayRequest holidayRequest) {
        if (holidayRequest == null) {
            return null;
        }
        Holiday holiday = new Holiday();
        holiday.setName(holidayRequest.getName());
        holiday.setPhoto(holidayRequest.getPhoto());
        holiday.setHolidayDate(holiday.getHolidayDate());

        return holiday;

    }

    public void update(Holiday holiday, HolidayRequest holidayRequest) {
        holiday.setPhoto(holidayRequest.getPhoto());
        holiday.setName(holidayRequest.getName());
        holiday.setHolidayDate(holidayRequest.getDate());
    }
}
