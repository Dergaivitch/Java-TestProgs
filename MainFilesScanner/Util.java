
/*
unsave beta exceptions-
*/

package ....

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.mozilla.universalchardet.UniversalDetector;
import org.w3c.dom.NamedNodeMap;

public class Util {

    public static class HashInfo implements Serializable {

        HashMap<String, Set<String>> prj_ver_hm;
        int[] itemi;
        LinkedHashSet<String> min_vrs;

        public HashInfo() {
            this.min_vrs = new LinkedHashSet();
            this.prj_ver_hm = new HashMap();
        }

    }

    static Logger log = Logger.getLogger(Util.class);

    public static String digestType = "";

    public static final int min_lenth = ;
    public static final int max_lenth = ;

    private static final String ext_list[] = {""};
    
    private static final String encode_temp_file = "";

//---------------------------OpenSourceBase-------------------------------------
    public static String serial_vers_db = "";

    public static String serial_db_mappath;
    public static String DB = "";

    public static String CB = "";

    public static String result_path = "";

    public static String last_dir = "";

    public static String prod_search = "";

    public static String ver_list = "";

    public static String base = "";

//------------os_db.xml-----------------------------------------------
    public static String underline = "";

    public static String name_field = "";

    public static String sha_field = "";

    public static String file_field = "";

    public static String version_field = "";

    public static String project_field = "";

    public static String ouf = "";

//---------------------CveBase-------------------------------------------------    
    public static String id_field = "";

    public static String score_field = "";

    public static String type_field = "";

    public static String vul_field = "";

    public static String num_field = "";

    public static String v_field = "";

    public static String cve_field = "";

    public static String nVul_field = "";

    //------------------------Result----------------------------
    public static String res_field = "";

    public static String res_projects_field = "";

    public static String res_p_name = "";

    public static String last_r_file = "";

    public static String help_info = "use -m | mode with c/a/u/h/i/d argument to choose mode\n"
            + "\n"
           

    public static File encodeFile(String fileName) throws IOException {
        byte[] buf = new byte[4096];
        java.io.FileInputStream fis_t = new java.io.FileInputStream(fileName);
        UniversalDetector detector = new UniversalDetector(null);

        int n_read;
        while ((n_read = fis_t.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, n_read);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        if ((encoding == null) || (encoding.equals("UTF-8"))) {
            return new File(fileName);
        } else {
            StringBuilder string_builder = new StringBuilder();

            try {
                try ( 
                        BufferedReader brin = new BufferedReader(new FileReader(fileName))) {
                    String s;
                    while ((s = brin.readLine()) != null) {
                        string_builder.append(s);
                        string_builder.append("\n");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File tempFile = new File(encode_temp_file);
            if (tempFile.exists()) {
                tempFile.delete();
            } else {
                tempFile.createNewFile();
            }

            String write_it_down = new String(string_builder.toString().getBytes(encoding), "UTF-8");
            try (FileWriter writeFile = new FileWriter(tempFile)) {
                writeFile.write(write_it_down);
            }
            detector.reset();
            return tempFile;
        }

    }

    public static void serialize(Object obj, String out_f) throws IOException {
//проверить out_f перед сериализацией
        FileOutputStream fos = new FileOutputStream(out_f);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        fos.close();
    }

    public static Object readSerializedObj(File in_f) throws IOException, ClassNotFoundException {

        FileInputStream fis = new FileInputStream(in_f);
        ObjectInputStream oin = new ObjectInputStream(fis);
        //TestSerial ts = (TestSerial) oin.readObject();
        return oin.readObject();
    }

    public static String getDigest(String fileName, boolean encoding, String digestType) throws IOException, NoSuchAlgorithmException {

        File tempFile = new File(fileName);
        if (encoding) {
            tempFile = encodeFile(fileName);
        }

        MessageDigest md = MessageDigest.getInstance(digestType);
        StringBuilder hexString;
        try (InputStream fis = new BufferedInputStream(new FileInputStream(tempFile))) {
            byte[] dataBytes = new byte[1024];
            int j = 0, k = 1024;
            int nread;
            while (fis.available() >= 1024) {
                while (j < k) {
                    nread = fis.read();
                    if (nread > 40) {

                        dataBytes[j] = (byte) nread;
                        j++;
                    } else {
                        k--;
                    }
                }
                md.update(dataBytes, 0, j);
                j = 0;
                k = 1024;
            }
            while (fis.available() != 0) {
                nread = fis.read();
                if (nread > 40) {
                    dataBytes[j] = (byte) nread;
                    j++;
                }

            }
            md.update(dataBytes, 0, j);
            byte[] mdbytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hexString = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }
        }

        return (hexString.toString());

    }

    public static org.w3c.dom.Document getDocument(String full_doc_path) throws SAXException, IOException, ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(full_doc_path);
    }

    public static org.w3c.dom.Document getDocument() throws SAXException, IOException, ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.newDocument();
    }

    public static void saveDocument(org.w3c.dom.Document document, String full_doc_path) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(full_doc_path));
        transformer.transform(domSource, streamResult);

    }

    public static void deleteLastResultFile() throws IOException {
        File ch_file = new File(result_path);
        if (ch_file.exists()) {
            InputStream del_stream = new FileInputStream(ch_file);
            InputStreamReader del_isr = new InputStreamReader(del_stream);
            BufferedReader del_br = new BufferedReader(del_isr);
            String del_path = del_br.readLine();
            File del_f = new File(del_path);
            if (del_f.exists()) {
                del_f.delete();
            }
        }
    }

    public static Set<String> getProjects(String base_path) throws SAXException, IOException, ParserConfigurationException {

        TreeSet<String> projects = new TreeSet();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.parse(base_path + DB);
        NodeList pnames = document.getElementsByTagName(project_field);

        for (int i = 0; i < pnames.getLength(); i++) {
            NamedNodeMap namedNodeMap = pnames.item(i).getAttributes();
            Node nodeAttr = namedNodeMap.getNamedItem(name_field);
            String c_prj = nodeAttr.getNodeValue();
            projects.add(c_prj);
        }

        System.out.println(projects);
        return projects;
    }

    public static Set makeFilesList(String szDir, Set<String> res_files_list) {


        File f = new File(szDir);
        String[] sDirList = f.list();

        for (String sDirList_cur : sDirList) {
            File f1 = new File(szDir + File.separator + sDirList_cur);
            if (f1.isFile()) {
                if (((f1.length() > min_lenth) && (f1.length() < max_lenth))) {
                    for (String t_ext : ext_list) {
                        if (sDirList_cur.toLowerCase().endsWith(t_ext)) {
                            res_files_list.add(f1.getAbsolutePath());
                            break;
                        }
                    }
                }

            } else {
                res_files_list.addAll(makeFilesList(szDir
                        + File.separator + sDirList_cur, res_files_list));
            }

        }
        return res_files_list;
    }

   
}
