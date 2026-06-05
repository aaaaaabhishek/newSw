package com.MT_MX.demo.utils;

import com.MT_MX.demo.iso20022.pacs_009_001_12.*;

public class MT202CovBuilder {

    public static String buildMT202COVFromDocument(Document document) {

        StringBuilder mt = new StringBuilder();

        FinancialInstitutionCreditTransferV12 fiToFi =
                document.getFICdtTrf();

        CreditTransferTransaction67 tx =
                fiToFi.getCdtTrfTxInf().get(0);

        GroupHeader131 grpHdr = fiToFi.getGrpHdr();

        // =========================
        // BLOCK 1 & 2
        // =========================
        String senderBic = getBic(grpHdr.getInstgAgt());
        String receiverBic = getBic(grpHdr.getInstdAgt());

        mt.append("{1:F01").append(senderBic).append("XXXX0000000000}");
        mt.append("{2:I202").append(receiverBic).append("XXXXN}");

        // =========================
        // BLOCK 3
        // =========================
        mt.append("{3:");
        if (tx.getPmtId() != null && tx.getPmtId().getUETR() != null) {
            mt.append("{121:").append(tx.getPmtId().getUETR()).append("}");
        }
        mt.append("}");

        // =========================
        // BLOCK 4
        // =========================
        mt.append("{4:\n");

        // :20
        mt.append(":20:").append(grpHdr.getMsgId()).append("\n");

        // :21
        if (tx.getPmtId() != null && tx.getPmtId().getEndToEndId() != null) {
            mt.append(":21:").append(tx.getPmtId().getEndToEndId()).append("\n");
        }

        // :32A
        if (tx.getIntrBkSttlmAmt() != null && tx.getIntrBkSttlmDt() != null) {
            String date = tx.getIntrBkSttlmDt().toString().substring(2, 10).replace("-", "");
            String ccy = tx.getIntrBkSttlmAmt().getCcy();
            String amt = tx.getIntrBkSttlmAmt().getValue().toPlainString().replace(".", ",");

            mt.append(":32A:").append(date).append(ccy).append(amt).append("\n");
        }

        // =====================================================
        //     UNDERLYING CUSTOMER TRANSFER
        // =====================================================
        CreditTransferTransaction68 underlying =
                tx.getUndrlygCstmrCdtTrf();

        // =========================
        // :50A / 50K (Ordering Customer)
        // =========================
        if (underlying != null) {

            if (underlying.getDbtrAgt() != null &&
                    underlying.getDbtrAgt().getFinInstnId() != null &&
                    underlying.getDbtrAgt().getFinInstnId().getBICFI() != null) {

                mt.append(":50A:")
                        .append(underlying.getDbtrAgt().getFinInstnId().getBICFI())
                        .append("\n");
            }

            else if (underlying.getDbtr() != null) {

                mt.append(":50K:");

                if (underlying.getDbtrAcct() != null &&
                        underlying.getDbtrAcct().getId() != null &&
                        underlying.getDbtrAcct().getId().getOthr() != null) {

                    mt.append("/")
                            .append(underlying.getDbtrAcct().getId().getOthr().getId())
                            .append("\n");
                }

                mt.append(underlying.getDbtr().getNm()).append("\n");

                if (underlying.getDbtr().getPstlAdr() != null) {
                    underlying.getDbtr().getPstlAdr().getAdrLine()
                            .forEach(a -> mt.append(a).append("\n"));
                }
            }
        }

        // =========================
        // BANK CHAIN (A/B/C/D)
        // =========================
        appendField(mt, "52", tx.getDbtrAgt(), null);
        appendField(mt, "53", tx.getIntrmyAgt1(), tx.getIntrmyAgt1Acct());
        appendField(mt, "56", tx.getIntrmyAgt2(), null);
        appendField(mt, "57", tx.getCdtrAgt(), null);
        appendField(mt, "58", tx.getCdtr(), null);

        // =========================
        // :59 (Beneficiary Customer)
        // =========================
        if (underlying != null && underlying.getCdtr() != null) {

            mt.append(":59:");

            if (underlying.getCdtrAcct() != null &&
                    underlying.getCdtrAcct().getId() != null &&
                    underlying.getCdtrAcct().getId().getOthr() != null) {

                mt.append("/")
                        .append(underlying.getCdtrAcct().getId().getOthr().getId())
                        .append("\n");
            }

            mt.append(underlying.getCdtr().getNm()).append("\n");

            if (underlying.getCdtr().getPstlAdr() != null) {
                underlying.getCdtr().getPstlAdr().getAdrLine()
                        .forEach(a -> mt.append(a).append("\n"));
            }
        }

        // =========================
        // :33B (Underlying Amount)
        // =========================
        if (underlying != null && underlying.getInstdAmt() != null) {

            mt.append(":33B:")
                    .append(underlying.getInstdAmt().getCcy())
                    .append(underlying.getInstdAmt().getValue().toPlainString().replace(".", ","))
                    .append("\n");
        }

        // =========================
        // :70 (Remittance)
        // =========================
        if (underlying != null &&
                underlying.getRmtInf() != null &&
                underlying.getRmtInf().getUstrd() != null &&
                !underlying.getRmtInf().getUstrd().isEmpty()) {

            mt.append(":70:")
                    .append(String.join(" ", underlying.getRmtInf().getUstrd()))
                    .append("\n");
        }


        // =========================
        // :72
        // =========================
        if (tx.getInstrForNxtAgt() != null &&
                !tx.getInstrForNxtAgt().isEmpty()) {

            mt.append(":72:");
            tx.getInstrForNxtAgt()
                    .forEach(i -> mt.append(i.getInstrInf()).append("\n"));
        }

        mt.append("-}");

        return mt.toString();
    }

    // =====================================================
    // A / B / C / D
    // =====================================================
    private static void appendField(StringBuilder mt,
                                    String tag,
                                    BranchAndFinancialInstitutionIdentification8 agent,
                                    CashAccount40 account) {

        if (agent == null || agent.getFinInstnId() == null) return;

        FinancialInstitutionIdentification23 fin = agent.getFinInstnId();

        // A
        if (fin.getBICFI() != null) {
            mt.append(":").append(tag).append("A:")
                    .append(fin.getBICFI()).append("\n");
            return;
        }

        // B
        if (account != null &&
                account.getId() != null &&
                account.getId().getOthr() != null) {

            mt.append(":").append(tag).append("B:/")
                    .append(account.getId().getOthr().getId())
                    .append("\n");
            return;
        }

        // C
        if (fin.getClrSysMmbId() != null) {
            mt.append(":").append(tag).append("C://")
                    .append(fin.getClrSysMmbId().getMmbId())
                    .append("\n");
            return;
        }

        // D
        if (fin.getNm() != null) {
            mt.append(":").append(tag).append("D:")
                    .append(fin.getNm()).append("\n");

            if (fin.getPstlAdr() != null) {
                fin.getPstlAdr().getAdrLine()
                        .forEach(a -> mt.append(a).append("\n"));
            }
        }
    }

    private static String getBic(BranchAndFinancialInstitutionIdentification8 agent) {
        if (agent != null &&
                agent.getFinInstnId() != null &&
                agent.getFinInstnId().getBICFI() != null) {
            return agent.getFinInstnId().getBICFI();
        }
        return "UNKNOWN";
    }
}