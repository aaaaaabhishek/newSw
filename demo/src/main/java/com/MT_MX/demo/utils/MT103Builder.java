package com.MT_MX.demo.utils;

import com.MT_MX.demo.iso20022.pacs.*;

import java.io.FileWriter;

public class MT103Builder {

    public static String buildMT103FromDocument(Document document) {
        StringBuilder mt = new StringBuilder();

        FIToFICustomerCreditTransferV13 fiToFi = document.getFIToFICstmrCdtTrf();
        CreditTransferTransaction70 tx = fiToFi.getCdtTrfTxInf().get(0); // assume 1 transaction
        GroupHeader131 grpHdr = fiToFi.getGrpHdr();

        // ------------------------ Block 1/2 BICs
        String senderBic = grpHdr.getInstgAgt() != null && grpHdr.getInstgAgt().getFinInstnId() != null ?
                grpHdr.getInstgAgt().getFinInstnId().getBICFI() : "UNKNOWNBIC";
        String receiverBic = grpHdr.getInstdAgt() != null && grpHdr.getInstdAgt().getFinInstnId() != null ?
                grpHdr.getInstdAgt().getFinInstnId().getBICFI() : "UNKNOWNBIC";

        // ------------------------ Block 1 & 2 placeholder
        mt.append("{1:F01").append(senderBic).append("XXXX0000000000}")
                .append("{2:I103").append(receiverBic).append("XXXXN}");

        // ------------------------ Block 3 (Optional)
        mt.append("{3:");
        // Field 121 → UETR
        PaymentIdentification13 pmtId = tx.getPmtId();
        if (pmtId != null && pmtId.getUETR() != null) {
            mt.append("{121:").append(pmtId.getUETR()).append("}");
        }
        // Field 108 → Bank Session/Reference
        if (grpHdr.getCreDtTm() != null) {
            mt.append("{108:").append(grpHdr.getMsgId()).append("}");
        }
        mt.append("}");

        mt.append("{4:\n");

        // ------------------------ 20 - Transaction Reference
        mt.append(":20:").append(grpHdr.getMsgId()).append("\n");

        // ------------------------ 23B - Bank Operation Code
        mt.append(":23B:CRED\n");

        // 32A
        if (tx.getIntrBkSttlmAmt() != null && tx.getIntrBkSttlmDt() != null) {
            String date = tx.getIntrBkSttlmDt().toString().substring(2, 10)
                    .replace("-", "");  // YYMMDD
            String ccy = tx.getIntrBkSttlmAmt().getCcy();
            String amount = tx.getIntrBkSttlmAmt()
                    .getValue()
                    .toPlainString()
                    .replace(".", ",");
            mt.append(":32A:")
                    .append(date)
                    .append(ccy)
                    .append(amount)
                    .append("\n");
        }
        // 33B
        if (tx.getInstdAmt() != null) {
            String ccy = tx.getInstdAmt().getCcy();
            String amount = tx.getInstdAmt()
                    .getValue()
                    .toPlainString()
                    .replace(".", ",");
            mt.append(":33B:")
                    .append(ccy)
                    .append(amount)
                    .append("\n");
        }


//        // ------------------------ 32A - Value Date / Currency / Amount
//        String date = tx.getIntrBkSttlmDt().toString().replace("-", "");
//        String ccy = tx.getIntrBkSttlmAmt().getCcy();
//        String amt = tx.getIntrBkSttlmAmt().getValue().toString().replace(".", ",");
//        mt.append(":32A:").append(date.substring(2)).append(ccy).append(amt).append("\n");



        // 50A (BIC only)
        if (tx.getDbtrAgt() != null &&
                tx.getDbtrAgt().getFinInstnId() != null &&
                tx.getDbtrAgt().getFinInstnId().getBICFI() != null) {

            mt.append(":50A:")
                    .append(tx.getDbtrAgt().getFinInstnId().getBICFI())
                    .append("\n");
        }

// 50K (account + name + address)
        else if (tx.getDbtr() != null) {

            mt.append(":50K:");
            if (tx.getDbtr().getNm() != null) {
                mt.append("/")
                        .append(tx.getDbtr().getNm()).append("\n");
            }
            // Account
            if (tx.getDbtrAcct() != null &&
                    tx.getDbtrAcct().getId() != null &&
                    tx.getDbtrAcct().getId().getOthr() != null) {

                mt.append(tx.getDbtrAcct().getId().getOthr().getId())
                        .append("\n");
            }



            // Address
            if (tx.getDbtr().getPstlAdr() != null) {
                for (String adr : tx.getDbtr().getPstlAdr().getAdrLine()) {
                    mt.append(adr).append("\n");
                }
            }
        }


        // ------------------------ 52A/D - Ordering Institution (optional)
        if (tx.getDbtrAgt() != null && tx.getDbtrAgt().getFinInstnId() != null) {

            FinancialInstitutionIdentification23 fin =
                    tx.getDbtrAgt().getFinInstnId();

            // 52A (BIC format)
            if (fin.getBICFI() != null && !fin.getBICFI().isBlank()) {

                mt.append(":52A:")
                        .append(fin.getBICFI())
                        .append("\n");
            }

            // 52D (Name + Address format)
            else if (fin.getNm() != null) {

                mt.append(":52D:")
                        .append(fin.getNm())
                        .append("\n");

                if (fin.getPstlAdr() != null) {
                    for (String adr : fin.getPstlAdr().getAdrLine()) {
                        mt.append(adr).append("\n");
                    }
                }
            }
        }
        // ------------------------ 57A - Account With Institution
        if (tx.getCdtrAgt() != null && tx.getCdtrAgt().getFinInstnId() != null) {
            mt.append(":57A:").append(tx.getCdtrAgt().getFinInstnId().getBICFI()).append("\n");
        }

        // ------------------------ 59 - Beneficiary Customer
        mt.append(":59:");
        if (tx.getCdtrAcct() != null && tx.getCdtrAcct().getId() != null && tx.getCdtrAcct().getId().getOthr() != null) {
            mt.append("/").append(tx.getCdtrAcct().getId().getOthr().getId()).append("\n");
        }
        mt.append(tx.getCdtr().getNm()).append("\n");
        if (tx.getCdtr().getPstlAdr() != null) {
            for (String line : tx.getCdtr().getPstlAdr().getAdrLine()) mt.append(line).append("\n");
        }

        // ------------------------ 70 - Remittance Information
        if (tx.getRmtInf() != null && tx.getRmtInf().getUstrd() != null && !tx.getRmtInf().getUstrd().isEmpty()) {
            mt.append(":70:").append(String.join(" ", tx.getRmtInf().getUstrd())).append("\n");
        }

        // ------------------------ 71A - Charges
        if (tx.getChrgBr() != null) {
            switch (tx.getChrgBr()) {
                case DEBT -> mt.append(":71A:OUR\n");
                case CRED -> mt.append(":71A:BEN\n");
                case SHAR -> mt.append(":71A:SHA\n");
                default -> mt.append(":71A:SHA\n");
            }
        }

// ------------------------ 71F / 71G - Optional Charges
        if (tx.getChrgsInf() != null && !tx.getChrgsInf().isEmpty()) {
            for (Charges16 ch : tx.getChrgsInf()) {
                if (ch.getAmt() != null) {
                    String cur = ch.getAmt().getCcy();
                    String val = ch.getAmt().getValue().toString().replace(".", ",");
                    // Map based on type
                    if (ch.getTp() != null && "SLEV".equals(ch.getTp().getCd())) {
                        mt.append(":71F:").append(cur).append(val).append("\n");
                    } else if (ch.getTp() != null && "RCVD".equals(ch.getTp().getCd())) {
                        mt.append(":71G:").append(cur).append(val).append("\n");
                    } else {
                        // fallback if type not set
                        mt.append(":71F:").append(cur).append(val).append("\n");
                    }
                }
            }
        }

        // ------------------------ 72 - Sender to Receiver Information
// ----------------------- Field 72 (Sender to Receiver Info)
        StringBuilder f72 = new StringBuilder();

// Creditor agent instructions
        if (tx.getInstrForCdtrAgt() != null) {
            for (InstructionForCreditorAgent3 inst : tx.getInstrForCdtrAgt()) {

                if (inst.getInstrInf() != null && !inst.getInstrInf().isBlank()) {

                    f72.append("/ACC/")
                            .append(inst.getInstrInf().trim())
                            .append("\n");
                }
            }
        }

// Next agent instructions
        if (tx.getInstrForNxtAgt() != null) {
            for (InstructionForNextAgent1 inst : tx.getInstrForNxtAgt()) {

                if (inst.getInstrInf() != null && !inst.getInstrInf().isBlank()) {

                    f72.append("/INS/")
                            .append(inst.getInstrInf().trim())
                            .append("\n");
                }
            }
        }

// Append to MT message
        if (!f72.isEmpty()) {
            mt.append(":72:")
                    .append(f72.toString());
        }

        mt.append("-}\n");

        return mt.toString();
    }

    // =====================================================
    // DETECT MX TYPE
    // =====================================================
    private String detectMxType(Document document) {
        if (document.getFIToFICstmrCdtTrf() != null) return "pacs.008";
        throw new RuntimeException("Unsupported MX type");
    }

    // =====================================================
    // IO
    // =====================================================
    private void writeToFile(String content, String path) {
        try (FileWriter w = new FileWriter(path)) {
            w.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
