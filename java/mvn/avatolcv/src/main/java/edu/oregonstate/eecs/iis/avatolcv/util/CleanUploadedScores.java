package edu.oregonstate.eecs.iis.avatolcv.util;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;

public class CleanUploadedScores {

    public static void main(String[] args) {
        
        CleanUploadedScores cleaner = new CleanUploadedScores();
        // to clean leafDev scores that have been uploaded
        cleaner.run("leaf apex angle");
    }
    public void run(String tagname){
        try {
            AvatolCVFileSystem.setRootDir("C:\\jed\\avatol\\git\\avatol_cv");
            BisqueWSClientImpl bisque = new BisqueWSClientImpl();
            bisque.authenticate("jedirv","Neton3plants**");
            List<String> ids = getImageIDs();
            for (String id : ids){
                cleanScore(bisque, id, tagname);
            }
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            System.out.println(ace.getMessage());
        }
        catch(BisqueWSException ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        
        
    }
    public void cleanScore(BisqueWSClientImpl bisque, String id, String tagname) throws BisqueWSException {
        bisque.reviseAnnotation(id, tagname, "");
    }
    public List<String> getImageIDs(){
        List<String> list = getRoots();
        List<String> ids = new ArrayList<String>();
        for (String s : list){
            String[] parts = ClassicSplitter.splitt(s, '_');
            String id = parts[0];
            ids.add(id);
        }
        return ids;
    }
    
    public List<String> getRoots(){
        List<String> list = new ArrayList<String>();
        list.add("00-3HPPsgoaeBaq2rDrGPnvhn_Alstoemeria-mcl73-mp");
        list.add("00-Reqqdvcx952DckWQcSRbe_AlismaPlantagoAquatica-MCL41-mod");
        list.add("00-oBhG8avxkAreD8YuAeugsM_Asparagus-asparagoides-0282");
        list.add("00-rmwqZ7ex3uSJGFsLabsuvU_AlocasiaViolacea-875a-0009-1mp");
        list.add("00-bqihy8xzUCD6epUfxmoDVj_Asparagus-asparagoides-0281");
        list.add("00-CB3WvWvyfpTh3KqP9jzuWa_Avetra-sempervirens-0038");
        list.add("00-gzxzMzpwAs8p8eBeVTckRk_Behnia-reticulata-0265");
        list.add("00-WtCEStwAWEjBC5FPz4tVPZ_Bomarea-hirtella-0065");
        list.add("00-ssNfn9BRHqGBznyxW7PDkd_Bomarea-hirtella-0067-1");
        list.add("00-BDywJw976eKJirCTuERbuR_BurbidgeaStenantha-mcl97-0306-mod");
        list.add("00-ztHppkof46G8FjkLWdrkNb_CalatheaZebrina-mcl20-0302");
        list.add("00-RVLizYbVPC7aj26qnwbGYF_CallisiaMultiflora-mcl118-0310-mod");
        list.add("00-bVZnKNevn62VvrguXxhn67_Camera-shots-001");
        list.add("00-zpAcdUjiSUQKFdprUto9vi_Asparagus-asparagoides-0286");
        list.add("00-zcv5xtVbwA3AMVQ4VvGkW5_Asparagus-asparagoides-0283");
        list.add("00-V4dNvJFmUQMuHzsNCM3mM3_Asparagus-falcatus-var");
        list.add("00-tYJNyqGUwLAZtw4znpiNHJ_Camera-shots-003");
        list.add("00-fy63cVcj63bB9kDVc3txDh_Asparagus-oxyacanthus-0291");
        list.add("00-D4SbTECLJcbxEG5vYd4AzN_Alstroemeria-pelegrina-0054");
        list.add("00-LgTKCcjfoyYjcG3ohuH8UC_Camera-shots-005");
        list.add("00-UCgKW7ong5s5gP9Uz8mhS4_Camera-shots-014");
        list.add("00-EVUzjuP2RKvnGG4rAxbjJR_Camera-shots-015");
        list.add("00-p68R7QuVLcNMR5RAZdsDyB_Camera-shots-021");
        list.add("00-RTxYGKKT6DJax4Fu6GqgtH_Camera-shots-036");
        list.add("00-Yqw37TUB7LBucJK2iCa375_Camera-shots-038");
        list.add("00-oRMNc5wuL8g4rq7PC3tywS_Camera-shots-039");
        list.add("00-NyVUN2UbDC8FxRXxb9xrp8_Camera-shots-040");
        list.add("00-aQicaUt2RPnmsCCe9a2SxQ_Camera-shots-044");
        list.add("00-P6p4NScGQ6FdYMBniQMsL7_Camera-shots-012");
        list.add("00-qHBiffY2FimbTgAGVnAuWj_Camera-shots-047");
        list.add("00-d8QGgBa4kNDMbuiwkC2kEF_Camera-shots-048");
        list.add("00-8nwkfS8n6bBMXvafjGcpmV_Camera-shots-049");
        list.add("00-GCXbB8ENj6tBxRc7TSRbWf_Camera-shots-051");
        list.add("00-ynSWs4F6Vv4rDsyayiStfG_Camera-shots-013");
        list.add("00-aNU9MaaeiFuuwSJ9xyw7PY_Camera-shots-022");
        list.add("00-hW2XQK8LWxrLuMV978zsCo_Camera-shots-023");
        list.add("00-xAHnP84bHekXTzi9etbLVe_Camera-shots-072");
        list.add("00-MAQHCtziu3ABQhacHmteGY_Camera-shots-075");
        list.add("00-gBTFykLBYhpbzuJybVGNr8_Camera-shots-077");
        list.add("00-pm8zdFf8qtiNvYnfPNg378_Camera-shots-076");
        list.add("00-4v7rquVm3f7CLBATdCMoZi_Chamaelirium-luteum-0147");
        list.add("00-EsfZsCcn9FDqBAkeFrsMBm_Camera-shots-081");
        list.add("00-dMCM3r9qek9hjMd8rCwbbf_clearings-002");
        list.add("00-vGUXTnsGLZgr5SwzLEyQoF_Camera-shots-080");
        list.add("00-vgd4D7sJcRuzB52DLLG3kZ_Camera-shots-079");
        list.add("00-V39tDaeyj6JZ4VGvkoXby7_clearings-003");
        list.add("00-zFJBivurxBrCbpF6pPstjY_clearings-007");
        list.add("00-Uijjfx8LUPSxBoEH9fdvp4_clearings-006");
        list.add("00-EsK9cf4XvCpyYiVMdtarLG_clearings-013");
        list.add("00-DNFhvKr5hfrHx5TNTD7dn3_clearings-014");
        list.add("00-NKDx6KpJFiuteTmkcJQocd_clearings-015");
        list.add("00-zZcFDrq2a4jF2DhPUsBz3k_clearings-017");
        list.add("00-tFj3Fqpfxv3qumUrE5jnpS_clearings-020");
        list.add("00-wnVvppkRExNb3EtHCWqoBZ_clearings-022");
        list.add("00-HWJykBwr9JyWTVh9bqzSGV_clearings-023");
        list.add("00-3CNJjBFqhxC3r9ajbXp9T6_clearings-019");
        list.add("00-h8QYBvccnVCchRmsEQvCd6_clearings-018");
        list.add("00-DR6hhMjcQpfWbFYjTDAUo4_clearings-025");
        list.add("00-HGZUuZjs8Ynmfe3TW2RaP7_clearings-024");
        list.add("00-6Gi99YjCYeXnNZWnGqEkXR_clearings-021");
        list.add("00-dzwN8ML2sgSs8hys68FRih_clearings-029");
        list.add("00-CW2XrnNnPzrcYPkzuYtF4h_Convallaria-mcl33-mp");
        list.add("00-iFWFHq79wyZvnBMuwLJw9k_clearings-026");
        list.add("00-CHfoqykGHtr44xydaUw7g8_ColocasiaEsculenta-SYSsn");
        list.add("00-GaJLHWazRxmLHpaWuSjEZ_clearings-027");
        list.add("00-JGxkej5BANWuLkh6hVEyTm_Croomia-pauciflora-0247");
        list.add("00-Z3pNgjXcts7fDF6f2UBB8L_Croomia-pauciflora-0245");
        list.add("00-6Q6yYfmt3ggNtYPQmkoLyA_Dioscorea-communis-0040");
        list.add("00-C2JZgwcU4DsRTYQLEJAscJ_Dioscorea-hexagona-0052");
        list.add("00-fCCRctRECTEkewfs59fkXb_Dioscorea-hexagona-0053");
        list.add("00-6W6gu79r9Gmn8se7mGQnuM_Dioscorea-communis-0041");
        list.add("00-XUMRpWGerqJMpvZndSQfpR_Disporum-lomogirosum-0091");
        list.add("00-iXucRQRzygWf48HC4ZykHV_Polygonatum-multiflorum-0303");
        list.add("00-hy7wUPoE8qbemdo5pR2EfQ_Ripogonum-album-0154");
        list.add("00-9subxxfY4zb5iBJnxy5Dpn_Smilax-herbacea-0197");
        list.add("00-mcjMnbLYkKyFSF3riCt6rL_Stemona-parviflora-0267");
        return list;
    }
    
    
}
