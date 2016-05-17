package ..../

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;
import static ru.cnpo.appchecker.cvedetector.Util.*;
public class Main {

    public class Mode {

        String mode;
        String data_path;
        String ver;
        String prj_path;
        String name;
        String db_path;

        Mode(String mode) {
            this.mode = mode;
        }

    }

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Options options = new Options();
            options.addOption("m", "mode", true, "select mode (-m | -mode)\nh | help\nu | update \nc "
                    + "| check \na | add \nd | delete \ni | info \n");
            options.addOption("p", "path", true, "path to project foulder");
            options.addOption("n", "name", true, "name of project");
            options.addOption("v", "version", true, "version of project");
            options.addOption("r", true, "result path");
            options.addOption("b", "base", true, "basepath");
            options.addOption("f", true, "filters");

            CommandLineParser parser = new BasicParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("m")) {
                log.info("\n_____________________________\n"
                        + "mode: " + cmd.getOptionValue("m") + "\n"
                        + "basepath: " + cmd.getOptionValue("b") + "\n"
                        + "_____________________________");
                switch (cmd.getOptionValue("m")) {
                    case "h":
                    case "help":
                        System.out.println(help_info);
                        return;
                    case "u":
                    case "update":
                        if (cmd.hasOption("b")) {
                            File t_db = new File(cmd.getOptionValue("b") + CB);
                            if (!t_db.isFile()) {
                                System.out.println("no cve base in " + cmd.getOptionValue("b"));
                                log.warn("no cve base in " + cmd.getOptionValue("b"));
                                return;
                            }
                            CveBase cveBase = new CveBase(cmd.getOptionValue("b"));
                            cveBase.update();

                        } else {
                            System.out.println("-b parameter not found");
                            log.warn("-b parameter not found");
                        }
                        return;
                    case "ub":
                        if (cmd.hasOption("b")) {
                            File t_db = new File(cmd.getOptionValue("b") + DB);
                            if (!t_db.exists()) {
                                log.warn("check -b param");
                                return;
                            }
                            OpenSourceBase openSourceBase = new OpenSourceBase(cmd.getOptionValue("b"));
                            openSourceBase.updatePrj();

                        } else {
                            log.warn("no arg -b");
                            System.out.println("no arg -b");
                        }
                        return;
                    case "a":
                    case "add":
                        if ((cmd.hasOption("b")) && (cmd.hasOption("p")) && (cmd.hasOption("n")) && (cmd.hasOption("v"))) {
                            File t_db = new File(cmd.getOptionValue("b") + DB);
                            File t_cb = new File(cmd.getOptionValue("b") + CB);
                            File t_p = new File(cmd.getOptionValue("p"));
                            if (!t_db.isFile() || !t_cb.isFile()) {
                                System.out.println("check -b param");
                                log.warn("check -b param");
                                return;
                            }
                            if (!t_p.isDirectory()) {
                                System.out.println("check -p param");
                                log.warn("check -p param");
                                return;
                            }
                            OpenSourceBase openSourceBase;
                            if (cmd.hasOption("f")) {
                                openSourceBase = new OpenSourceBase(cmd.getOptionValue("b"), cmd.getOptionValue("n"),
                                        cmd.getOptionValue("v"), cmd.getOptionValue("p"), cmd.getOptionValue("f").split(","));
                            } else {
                                openSourceBase = new OpenSourceBase(cmd.getOptionValue("b"), cmd.getOptionValue("n"),
                                        cmd.getOptionValue("v"), cmd.getOptionValue("p"), null);
                            }
                            openSourceBase.Add();
                        } else {
                            System.out.println("not found args -b -n -p -v");
                            log.warn("not found args -b -n -p -v");
                        }
                        return;
                    case "i2":
                        if (cmd.hasOption("b")) {
                            File t_db = new File(cmd.getOptionValue("b") + DB);
                            File t_cb = new File(cmd.getOptionValue("b") + CB);
                            if (!t_db.isFile() || !t_cb.isFile()) {
                                System.out.println("check -b param");
                                log.warn("check -b param");
                                return;
                            }
                            CveBase cveBase = new CveBase(cmd.getOptionValue("b"));
                            cveBase.getStat();
                        } else {
                            System.out.println("no -b arg found");
                            log.warn("no -b arg found");
                        }
                        return;
                    case "i1":
                        if (cmd.hasOption("b")) {
                            File t_db = new File(cmd.getOptionValue("b") + DB);
                            File t_cb = new File(cmd.getOptionValue("b") + CB);
                            if (!t_db.isFile() || !t_cb.isFile()) {
                                System.out.println("check -b param");
                                log.warn("check -b param");
                                return;
                            }
                            OpenSourceBase openSourceBase = new OpenSourceBase(cmd.getOptionValue("b"));
                            openSourceBase.getStat();
                        } else {
                            System.out.println("no -b arg found");
                            log.warn("no -b arg found");
                        }
                        return;
                    case "i":
                    case "info":
                        if (cmd.hasOption("b")) {
                            File t_db = new File(cmd.getOptionValue("b") + DB);
                            File t_cb = new File(cmd.getOptionValue("b") + CB);
                            if (!t_db.isFile() || !t_cb.isFile()) {
                                System.out.println("check -b param");
                                log.warn("check -b param");
                                return;
                            }
                            CveBase cveBase = new CveBase(cmd.getOptionValue("b"));
                            OpenSourceBase openSourceBase = new OpenSourceBase(cmd.getOptionValue("b"));
                            cveBase.getStat();
                            openSourceBase.getStat();

                        } else {
                            System.out.println("no -b arg found");
                            log.warn("no -b arg found");
                        }
                        return;
                    case "c":
                    case "check":
                        if ((cmd.hasOption("b")) && (cmd.hasOption("p")) && (cmd.hasOption("r"))) {
                            OpenSourceBase openSourceBase = new OpenSourceBase(cmd.getOptionValue("b"), cmd.getOptionValue("p"), cmd.getOptionValue("r"));
                            openSourceBase.check();
                        } else {
                            System.out.println("no -b -p -r args found");
                            log.warn("no -b -p -r args found");
                        }
                        return;
                    case "d":
                    case "delete":
                        if ((cmd.hasOption("b")) && (cmd.hasOption("n"))) {
                            File t_db = new File(cmd.getOptionValue("b") + DB);
                            File t_cb = new File(cmd.getOptionValue("b") + CB);
                            if (!t_db.isFile() || !t_cb.isFile()) {
                                System.out.println("check -b param");
                                log.warn("check -b param");
                                return;
                            }
                            OpenSourceBase openSourceBase = new OpenSourceBase(cmd.getOptionValue("b"));
                            CveBase cveBase = new CveBase(cmd.getOptionValue("b"));
                            openSourceBase.delete(cmd.getOptionValue("n"));
                            cveBase.delete(cmd.getOptionValue("n"));
                        } else {
                            System.out.println("no -b -n args");
                            log.warn("no -b -n args");
                        }
                        return;
                    default:
                        System.out.println("mods: i|a|d|c|u|h  use help mod ");
                        log.warn("mods: i|a|d|c|u|h  use help mod ");

                }
            } else {
                System.out.println("no mode found");
                log.warn("\n_____________________________\nno mode found\n_____________________________");
            }

        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "excep1", ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "excep2", ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "excep3", ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "excep4", ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "excep5", ex);
        } catch (SAXException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "excep6", ex);
        }

    }

}
