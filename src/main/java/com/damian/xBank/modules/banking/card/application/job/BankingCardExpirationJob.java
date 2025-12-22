package com.damian.xBank.modules.banking.card.application.job;

import com.damian.xBank.modules.banking.card.application.service.BankingCardService;
import com.damian.xBank.modules.banking.card.domain.entity.BankingCard;
import com.damian.xBank.modules.banking.card.domain.enums.BankingCardStatus;
import com.damian.xBank.modules.banking.card.infra.repository.BankingCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Component
public class BankingCardExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(BankingCardExpirationJob.class);
    private final BankingCardRepository bankingCardRepository;
    private final BankingCardService bankingCardService;

    public BankingCardExpirationJob(
            BankingCardRepository bankingCardRepository,
            BankingCardService bankingCardService
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.bankingCardService = bankingCardService;
    }


    /**
     * Set EXPIRED status for every expired card
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void disableExpiredCards() {
        log.info("Checking for expired cards...");
        Set<BankingCard> expiredCards =
                bankingCardRepository.findByCardStatusNotAndExpiredDateLessThanEqual(
                        BankingCardStatus.EXPIRED,
                        LocalDate.now()
                );

        if (expiredCards.isEmpty()) {
            log.info("No expired cards found");
            return;
        }

        log.info("Found {} expired cards", expiredCards.size());
        for (BankingCard bankingCard : expiredCards) {
            bankingCard.setCardStatus(BankingCardStatus.EXPIRED);
        }

        bankingCardRepository.saveAll(expiredCards);
    }
}
