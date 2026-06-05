package com.MT_MX.demo.utils;

import com.MT_MX.demo.iso20022.pacs_009_001_12.*;

public class MT202Builder {

    public static String buildMT202FromDocument(Document document) {

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
        if(tx.getPmtTpInf().getSvcLvl().get(0).getCd()!=null){
            String pmtTpInf=tx.getPmtTpInf().getSvcLvl().get(0).getCd();

            mt.append("{111:").append(pmtTpInf)
                    .append("}");
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

        // =========================
        // FIELD MAPPINGS (A/B/C/D)
        // =========================
        appendField(mt, "52", tx.getDbtrAgt(), null);
        appendField(mt, "53", tx.getIntrmyAgt1(), tx.getIntrmyAgt1Acct());
        appendField(mt, "56", tx.getIntrmyAgt2(), null);
        appendField(mt, "57", tx.getCdtrAgt(), null);
        appendField(mt, "58", tx.getCdtr(), null);

        // =========================
        // FIELD 72
        // =========================
        if (tx.getInstrForNxtAgt() != null && !tx.getInstrForNxtAgt().isEmpty()) {
            mt.append(":72:");
            tx.getInstrForNxtAgt()
                    .forEach(i -> mt.append(i.getInstrInf()).append("\n"));
        }
        if (tx.getInstrForCdtrAgt() != null && !tx.getInstrForCdtrAgt().isEmpty()) {
            mt.append(":72:");

            // We use a counter because Tag 72 is limited to 6 lines total
            int totalLines = 0;

            for (InstructionForCreditorAgent3 i : tx.getInstrForCdtrAgt()) {
                String text = i.getInstrInf();
                if (text == null || text.isEmpty()) continue;

                int len = text.length();

                mt.append(text.substring(0, Math.min(len, 35))).append("\n");
                totalLines++;

                if (len > 35 && totalLines < 6) {
                    mt.append("//").append(text.substring(35, Math.min(len, 70))).append("\n");
                    totalLines++;
                }

                if (len > 70 && totalLines < 6) {
                    mt.append("///").append(text.substring(70, Math.min(len, 105))).append("\n");
                    totalLines++;
                }
                if (len > 105 && totalLines < 6) {
                    mt.append("////").append(text.substring(105, Math.min(len, 140))).append("\n");
                    totalLines++;
                }
                if (len > 140 && totalLines < 6) {
                    mt.append("/////").append(text.substring(140, Math.min(len, 175))).append("\n");
                    totalLines++;
                }
                if (len > 175 && totalLines < 6) {
                    mt.append("//////").append(text.substring(175, Math.min(len, 175))).append("\n");
                    totalLines++;
                }



                if (totalLines >= 6) break;
            }
        }
        mt.append("-}");

        return mt.toString();
    }

    private static void appendField(StringBuilder mt,
                                    String tag,
                                    BranchAndFinancialInstitutionIdentification8 agent,
                                    CashAccount40 account) {

        if (agent == null || agent.getFinInstnId() == null) return;

        FinancialInstitutionIdentification23 fin = agent.getFinInstnId();

        // ===== A (BIC)
        if (fin.getBICFI() != null && !fin.getBICFI().isBlank()) {
            mt.append(":").append(tag).append("A:")
                    .append(fin.getBICFI()).append("\n");
            return;
        }

        // ===== B (Account)
        if (account != null &&
                account.getId() != null &&
                account.getId().getOthr() != null &&
                account.getId().getOthr().getId() != null) {

            mt.append(":").append(tag).append("B:/")
                    .append(account.getId().getOthr().getId())
                    .append("\n");
            return;
        }

        // ===== C (Clearing System)
        if (fin.getClrSysMmbId() != null &&
                fin.getClrSysMmbId().getMmbId() != null) {

            mt.append(":").append(tag).append("C://")
                    .append(fin.getClrSysMmbId().getMmbId())
                    .append("\n");
            return;
        }

        // ===== D (Name + Address)
        if (fin.getNm() != null) {

            mt.append(":").append(tag).append("D:")
                    .append(fin.getNm()).append("\n");

            if (fin.getPstlAdr() != null &&
                    fin.getPstlAdr().getAdrLine() != null) {

                fin.getPstlAdr().getAdrLine()
                        .forEach(a -> mt.append(a).append("\n"));
            }
        }
    }

    // =====================================================
    // Helper
    // =====================================================
    private static String getBic(BranchAndFinancialInstitutionIdentification8 agent) {
        if (agent != null &&
                agent.getFinInstnId() != null &&
                agent.getFinInstnId().getBICFI() != null) {
            return agent.getFinInstnId().getBICFI();
        }
        return "UNKNOWN";
    }
}