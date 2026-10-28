import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ADPPayroll {
    private static Map<String, List<String>> employeeMap;
    //private static Map<String, Map<String, String>> employeeMap;
    //private static Map<String, Map<String,String>> employeeJobMap;
    private static List<String> employeeJobEntry;

    //private static FileHandler fh = null;

    // processEmployee(String first, String last, String id, BufferedReader br)
    // Employee time card fields:
        // Field 0 = Date
        // Field 1 = empty
        // Field 2 = Job
        // Field 3 = Time In
        // Field 4 = Time out
        // Field 5 = empty
        // Field 6 = empty
        // Field 7 = Regular (Hours)
        // Field 8 = OT (Over Time)
        // Field 9 = Ext (??)
        // Field 10 = empty
        // Field 11 = Total Hrs
        // Field 12 = Paid Breaks
        // Field 13 = empty
        // Field 14 = Unpaid Breaks
        // Field 15 = Rate
        // Field 16 = Total Pay
        // Field 17 = empty
        // Field 18 = Non-Cash Tips
        // Field 19 = empty
        // Field 20 = Declared Tips
    public static void processEmployee(String first, String last, String id, BufferedReader br) {
        String[] tokens;
        String line;
        float teamMemberHours = 0;
        float teamMemberOT = 0;
        float teamCaptainHours = 0;
        float teamCaptainOT = 0;
        float ownerHours = 0;
        float ownerHoursOT = 0;

        try {
            employeeJobEntry = new ArrayList<String>();

            String position = "";
            while((line = br.readLine()) != null) {
                line = line.replaceAll("\\$", "");
                tokens = line.split(",");

                // need to skip lines that have either
                // Total Team Captain
                // Total Team Member
                // Saving employee name, position, regular hours, overtime hours
                if(!tokens[0].equals("Totals:")) {
                    if(!tokens[0].contains("Total ")) {
                        if(tokens[2].equals("Team Member")) {
                            position = tokens[2];
                            teamMemberHours = teamMemberHours + Float.parseFloat(tokens[7]);
                            teamMemberOT = teamMemberOT + Float.parseFloat(tokens[8]);
                        } else if(tokens[2].equals("Team Captain")) {
                            position = tokens[2];
                            teamCaptainHours = teamCaptainHours + Float.parseFloat(tokens[7]);
                            teamCaptainOT = teamCaptainOT + Float.parseFloat(tokens[8]);
                        } else if(tokens[2].equals("Owner")) {
                            position = tokens[2];
                            ownerHours = ownerHours + Float.parseFloat(tokens[7]);
                            ownerHoursOT = ownerHoursOT + Float.parseFloat(tokens[8]);
                        }
                    } else {
                        employeeJobEntry.add(id + "," + job + "," + teamMemberHours + "," + teamMemberOT);
                        position = "";
                    }
                
                } else {
                    //System.out.println("JOB = " + job);
                    employeeJobEntry.add(id + "," + job + "," + teamMemberHours + "," + teamMemberOT);
                    position = "";

                    //employeeJobEntry.add(id + ',' + tokens[0] + ',' +  tokens[2] + ',' + tokens[3]
                        //+ ',' + tokens[4] + ',' + tokens[7] + ',' +  + ',' + tokens[9] + ','
                        //+ tokens[11] + ',' + tokens[12] + ',' + tokens[14] + ',' + tokens[15]
                        //+ ',' + tokens[16] + ',' + tokens[18] + ',' + tokens[20]);

                    //regularHours = 0;
                    // skip line containing Signature
                    br.readLine();
                    break;
                }
            }
            employeeMap.put(first + "," + last, employeeJobEntry);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // saveTimeCard()
    // Save the processed employee time card so it can be uploaded into the payroll tool
    public static void saveTimeCard() {
        List<String> timeEntry = new ArrayList<String>();
        Map<String, List<String>> sorted = new TreeMap<>(employeeMap);

        File saveFile = getFile("Save Payroll Output File");
        
        //getDateTime();

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(saveFile));
            writer.write("First Name,Last Name,Payroll ID,Date,Job,Time In,Time Out,Regular,");
            writer.write("OT,Ext,Total Hours,Paid Break,Unpaid Break,Rate,Total Pay,Non-Cash,");
            writer.write("Declared Tips,Tip Share\n");
            for(String employee : sorted.keySet()) {
                timeEntry = sorted.get(employee);
                for(String timeRecord : timeEntry) {
                    writer.write(employee + ',' + timeRecord + '\n');
                } 
            } 
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // getFile(String title)
    // Get the employee time card file
    public static File getFile(String title) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.setDialogTitle(title);

        int response = fileChooser.showOpenDialog(null);
        File file = fileChooser.getSelectedFile();
        if(response == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        } else if(response == JFileChooser.CANCEL_OPTION) {
            System.exit(0);
        }

        return file;
    }

    // validateTimeCardFile()
    // ensure that the file selected is a timecard file
    public static boolean validateTimeCardFile(BufferedReader br) {
        String[] firstLine = null;

        try {
            firstLine = br.readLine().split(",");
            if(!firstLine[0].equals("Employee Timecard")) {
                JOptionPane.showMessageDialog(
                null,
                "Not an Employee Timecard File",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return firstLine[0].equals("Employee Timecard");
    }

    public static void main(String[] args) throws Exception {
        employeeMap = new HashMap<String, List<String>>();

        try {
            // get employee timecard file
            File file = getFile("Select Employee Time Card File");
            
            FileReader input = new FileReader(file);
            BufferedReader br = new BufferedReader(input);

            String inputLine;
            String firstName = "";
            String lastName =  "";
            String payrollID = "None";

            if(!validateTimeCardFile(br)) {
                System.exit(-1);
            }

            // process each employee in the timecard file
            while((inputLine = br.readLine()) != null) {
                String[] splits = inputLine.split(",");

                if(splits[0].equals("")) {
                    firstName = splits[2].replace("\"", "");
                    lastName = splits[1].replace("\"", "");;
                    br.readLine();
                    processEmployee(firstName, lastName, payrollID, br);                    
                }
            }

            // close timecard file
            br.close();


        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
