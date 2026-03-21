package com.damian.xBank.modules.banking.card.application.scheduler;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Job que cambia el estado de las tarjetas a EXPIRED.
 */
@Component
public class BankingCardExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(BankingCardExpirationJob.class);
    private final BankingCardRepository bankingCardRepository;

    public BankingCardExpirationJob(
        BankingCardRepository bankingCardRepository
    ) {
        this.bankingCardRepository = bankingCardRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Cada noche ...
    public void disableExpiredCards() {
        log.info("Checking for expired cards...");

        Pageable pageable = PageRequest.of(0, 100);
        Page<BankingCard> page;

        do {
            page = bankingCardRepository.findExpiredCards(
                BankingCardStatus.EXPIRED,
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                pageable
            );

            for (BankingCard bankingCard : page.getContent()) {
                bankingCard.expired();
            }

            pageable = pageable.next();

        } while (page.hasNext());
    }
}
