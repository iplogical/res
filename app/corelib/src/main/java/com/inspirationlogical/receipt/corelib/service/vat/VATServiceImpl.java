package com.inspirationlogical.receipt.corelib.service.vat;

import com.inspirationlogical.receipt.corelib.model.entity.VAT;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;
import com.inspirationlogical.receipt.corelib.repository.VATRepository;
import com.inspirationlogical.receipt.corelib.repository.VATSerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VATServiceImpl implements VATService {

    @Autowired
    private VATRepository vatRepository;

    @Autowired
    private VATSerieRepository vatSerieRepository;

    @Override
    public VATSerie findValidVATSerie() {
        return vatSerieRepository.findFirstByStatus(VATStatus.VALID);
    }

}
