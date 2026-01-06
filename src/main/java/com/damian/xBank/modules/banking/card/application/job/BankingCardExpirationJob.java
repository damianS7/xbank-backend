package com.damian.xBank.modules.banking.card.application.job;

import com.damian.xBank.modules.banking.card.domain.model.BankingCard;
import com.damian.xBank.modules.banking.card.domain.model.BankingCardStatus;
import com.damian.xBank.modules.banking.card.domain.service.BankingCardDomainService;
import com.damian.xBank.modules.banking.card.infrastructure.repository.BankingCardRepository;
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
    private final BankingCardDomainService bankingCardDomainService;

    public BankingCardExpirationJob(
            BankingCardRepository bankingCardRepository,
            BankingCardDomainService bankingCardDomainService
    ) {
        this.bankingCardRepository = bankingCardRepository;
        this.bankingCardDomainService = bankingCardDomainService;
    }


    /**
     * Set EXPIRED status for every expired card
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void disableExpiredCards() {
        log.info("Checking for expired cards...");
        Set<BankingCard> expiredCards =
                bankingCardRepository.findByStatusNotAndExpiredDateLessThanEqual(
                        BankingCardStatus.EXPIRED,
                        LocalDate.now()
                );

        if (expiredCards.isEmpty()) {
            log.info("No expired cards found");
            return;
        }

        log.info("Found {} expired cards", expiredCards.size());
        for (BankingCard bankingCard : expiredCards) {
            bankingCard.setStatus(BankingCardStatus.EXPIRED);
        }

        bankingCardRepository.saveAll(expiredCards);
    }
}
