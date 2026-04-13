package com.skillswap.messaging;

import com.skillswap.messaging.client.booking.BookingClient;
import com.skillswap.messaging.client.booking.BookingInternalDTO;
import com.skillswap.messaging.exception.ConflictException;
import com.skillswap.messaging.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class MessageServiceBookingTest {

    @Autowired
    private MessageService messageService;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createThreadRejectedWhenBookingNotConfirmed() {
        when(bookingClient.getBooking(99L)).thenReturn(new BookingInternalDTO(99L, 1L, 2L, "PENDING"));
        assertThrows(ConflictException.class, () -> messageService.createThread(99L));
    }
}
