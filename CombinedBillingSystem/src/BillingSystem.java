import java.security.SecureRandom;
import java.util.*;
import java.io.*;
import java.nio.file.Paths;

public class BillingSystem {
    static Scanner input = new Scanner(System.in);
    static SecureRandom random = new SecureRandom();
    static ClientRecord[][][][][] CBS = new ClientRecord[4][4][4][3][3];

    public static void main(String[] args) throws IOException {
        BufferedReader nameReader = new BufferedReader(new FileReader("Names"));
        int customer = 100000;
        for (byte sector = 0; sector < CBS.length; sector++) {
            for (byte sub = 0; sub < CBS[sector].length; sub++) {
                for (byte st = 0; st < CBS[sector][sub].length; st++) {
                    for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                        for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                            CBS[sector][sub][st][house][portion] = new ClientRecord();
                            CBS[sector][sub][st][house][portion].cID = customer++; // automatically assigns customer ID
                                                                                   // to each address
                            CBS[sector][sub][st][house][portion].name = nameReader.readLine();
                            for (int i = 0; i < 5; i++) {
                                CBS[sector][sub][st][house][portion].prevUnits[i] = 0;
                                switch (i) {
                                    case 0:
                                        CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(200);
                                        CBS[sector][sub][st][house][portion].billAmount[i] = gasBill(0,
                                                CBS[sector][sub][st][house][portion].newUnits[i]);
                                        break;
                                    case 1:
                                        CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(500);
                                        CBS[sector][sub][st][house][portion].billAmount[i] = electricityBill(0,
                                                CBS[sector][sub][st][house][portion].newUnits[i]);
                                        break;
                                    case 2:
                                        CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(3000);
                                        CBS[sector][sub][st][house][portion].billAmount[i] = waterBill(
                                                CBS[sector][sub][st][house][portion].newUnits[i]);
                                        break;
                                    case 3:
                                        CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(500);
                                        CBS[sector][sub][st][house][portion].billAmount[i] = phoneBill(
                                                CBS[sector][sub][st][house][portion].newUnits[i]);
                                        break;
                                    case 4:
                                        CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(500);
                                        CBS[sector][sub][st][house][portion].billAmount[i] = internetBill(
                                                CBS[sector][sub][st][house][portion].newUnits[i]);
                                        break;
                                }
                            }
                            writeRecords("Jan2022", CBS[sector][sub][st][house][portion].name,
                                    CBS[sector][sub][st][house][portion].cID,
                                    CBS[sector][sub][st][house][portion].prevUnits,
                                    CBS[sector][sub][st][house][portion].newUnits,
                                    CBS[sector][sub][st][house][portion].billAmount);
                        }
                    }
                }
            }
        }
        nameReader.close();
        generateFiles();
        showMenu();
    }

    // gives users options to choose which action to perform
    public static void showMenu() throws IOException {
        System.out.println(
                "\n1: Add Records\n2: Delete Records\n3: Search Records\n4: Modify Records\n5: Generate Reports\n6: Exit");
        byte choice, displayOp;
        do {
            System.out.print("Select Option: ");
            choice = input.nextByte();
            input.nextLine();
            switch (choice) {
                case 1:
                    addRecords();
                    break;
                case 2:
                    deleteRecords();
                    break;
                case 3:
                    searchRecords();
                    break;
                case 4:
                    modifyRecords();
                    break;
                case 5:
                    System.out.println(
                            "\n1: Client Report\n2: Report of a Sector\n3: Report of a Sub-Sector\n4: Report of a Street\n5: Report of a House\n6: Monthly Report\n7: Complete Yearly Report\n8: Complete half-yearly Report");
                    System.out.print("Select Option: ");
                    do {
                        displayOp = input.nextByte();
                        switch (displayOp) {
                            case 1:
                                clientReport();
                                break;
                            case 2:
                                sectorReport();
                                break;
                            case 3:
                                subSectorReport();
                                break;
                            case 4:
                                streetReport();
                                break;
                            case 5:
                                houseReport();
                                break;
                            case 6:
                                monthlyReport();
                                break;
                            default:
                                System.out.println("Invalid Input");
                        }
                    } while ((displayOp < 1) || (displayOp > 8));
                    break;
                case 6:
                    System.exit(0);
                    break;
                default:
                    System.out.println("\nInvalid Input");
                    break;
            }
        } while ((choice < 1) || (choice > 6));
    }

    // allows to add a specific record if not pre-existing in the array
    public static void addRecords() throws IOException {
        byte sector, sub, st, house, portion, recordMonth;
        do {
            System.out.print(
                    "1:Jan   2:Feb   3:Mar  4:Apr   5:May   6:Jun\n7:Jul   8:Aug    9:Sep   10:Oct   11:Nov   12:Dec\nAdd record for month: ");
            recordMonth = input.nextByte();
        } while ((recordMonth < 1) || (recordMonth > 12));
        String file = monthChooser(recordMonth);
        try {
            System.out.println("Enter correct address below");
            System.out.print("0:'A'    1:'B'   2:'C'   3:'D'   4:'E'\n5:'F'    6:'G'   7:'H'   8:'I'   9:'J'\nSector:");
            sector = input.nextByte();
            input.nextLine();
            System.out.print("Sub-Sector: ");
            sub = input.nextByte();
            input.nextLine();
            System.out.print("Street: ");
            st = input.nextByte();
            input.nextLine();
            System.out.print("House no: ");
            house = input.nextByte();
            input.nextLine();
            System.out.print("0:Ground Floor    1:First Floor   2:Second Floor\nPortion: ");
            portion = input.nextByte();
            input.nextLine();
            System.out.printf("ADDRESS  Sector %s, Sub-sector %d, Street# %d, House# %d, Portion %d%n",
                    (char) (sector + 65), sub, st, house, portion);

            if (CBS[sector][sub][st][house][portion].name != null) {
                System.out.println("Record already exists!\n1: Display Record?\n2: Modify Record?\n3: Cancel");
                byte choice;
                do {
                    choice = input.nextByte();
                    input.nextLine();
                    switch (choice) {
                        case 1:
                            displayRecord(CBS[sector][sub][st][house][portion].cID);
                            break;
                        case 2:
                            modifyRecords();
                            break;
                        case 3:
                            return;
                        default:
                            System.out.println("\nInvalid input! Try again: ");
                            break;
                    }
                } while ((choice < 1) || (choice > 3));
            } else {
                System.out.print("Enter client name: ");
                CBS[sector][sub][st][house][portion].name = input.nextLine();
                System.out.print("Client ID: " + CBS[sector][sub][st][house][portion].cID);

                System.out.println("\nEnter your meter readings for the following facilities");
                for (int i = 0; i <= 4; i++) {
                    switch (i) {
                        case 0:
                            System.out.println("Gas: ");
                            break;
                        case 1:
                            System.out.println("Electricity: ");
                            break;
                        case 2:
                            System.out.println("Water: ");
                            break;
                        case 3:
                            System.out.println("Phone: ");
                            break;
                        case 4:
                            System.out.println("Internet: ");
                            break;
                    }
                    System.out.print("Previous month: ");
                    CBS[sector][sub][st][house][portion].prevUnits[i] = input.nextInt();
                    System.out.print("Current month only: ");
                    CBS[sector][sub][st][house][portion].newUnits[i] = input.nextInt();
                    input.nextLine();
                    if (i <= 1) {
                        CBS[sector][sub][st][house][portion].newUnits[i] += CBS[sector][sub][st][house][portion].prevUnits[i];
                    }
                    switch (i) {
                        case 0:
                            CBS[sector][sub][st][house][portion].billAmount[i] = gasBill(
                                    CBS[sector][sub][st][house][portion].prevUnits[i],
                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                            break;
                        case 1:
                            CBS[sector][sub][st][house][portion].billAmount[i] = electricityBill(
                                    CBS[sector][sub][st][house][portion].prevUnits[i],
                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                            break;
                        case 2:
                            CBS[sector][sub][st][house][portion].billAmount[i] = waterBill(
                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                            break;
                        case 3:
                            CBS[sector][sub][st][house][portion].billAmount[i] = phoneBill(
                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                            break;
                        case 4:
                            CBS[sector][sub][st][house][portion].billAmount[i] = internetBill(
                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                            break;
                    }
                }
                writeRecords(file, CBS[sector][sub][st][house][portion].name, CBS[sector][sub][st][house][portion].cID,
                        CBS[sector][sub][st][house][portion].prevUnits, CBS[sector][sub][st][house][portion].newUnits,
                        CBS[sector][sub][st][house][portion].billAmount);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("\nInvalid Input! Please try again");
            showMenu();
        }
        showMenu();
    }

    // allows modification of the name of preexisting record in file of any month
    public static void modifyRecords() throws IOException {
        File temp = new File("C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Temp.txt");
        byte recordMonth;
        do {
            System.out.print(
                    "1:Jan   2:Feb   3:Mar  4:Apr   5:May   6:Jun\n7:Jul   8:Aug    9:Sep   10:Oct   11:Nov   12:Dec\nModify record for month: ");
            recordMonth = input.nextByte();
            input.nextLine();
        } while ((recordMonth < 1) || (recordMonth > 12));
        String file = monthChooser(recordMonth);
        File thisMonth = new File(file);
        int clientID;
        do {
            System.out.print("Enter client ID: ");
            clientID = input.nextInt();
            input.nextLine();
        } while ((clientID < 100000) || (clientID > 124000));
        for (byte sector = 0; sector < CBS.length; sector++) {
            for (byte sub = 0; sub < CBS[sector].length; sub++) {
                for (byte st = 0; st < CBS[sector][sub].length; st++) {
                    for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                        for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                            if (clientID == CBS[sector][sub][st][house][portion].cID) {
                                String modify = CBS[sector][sub][st][house][portion].cID + "";
                                String record;
                                // BufferedReader br = new BufferedReader(new FileReader(file));
                                // System.out.println("EXISTING RECORD:");
                                // while((record = br.readLine()) != null) {
                                // if (modify.equalsIgnoreCase(record)) {
                                // System.out.println("Client ID: "+record);
                                // System.out.println("Name: "+br.readLine());
                                // for(int i =1;i<=5;i++){
                                // StringTokenizer s = new StringTokenizer(br.readLine(),"|");
                                // s.nextToken();
                                // switch (i){
                                // case 1: System.out.print("GAS|| ");break;
                                // case 2: System.out.print("ELECTRICITY|| ");break;
                                // case 3: System.out.print("WATER|| ");break;
                                // case 4: System.out.print("PHONE|| ");break;
                                // case 5: System.out.print("INTERNET|| ");break;
                                // }
                                // System.out.println("Previous reading: "+s.nextToken()+" New reading:
                                // "+s.nextToken());
                                // }
                                // }
                                // }
                                // br.close();
                                System.out.println("\nNEW RECORD:");
                                System.out.print("Name: ");
                                CBS[sector][sub][st][house][portion].name = input.nextLine();
                                System.out.println(
                                        "\nEnter your meter readings (current month) for the following facilities >> ");
                                for (int i = 0; i <= 4; i++) {
                                    switch (i) {
                                        case 0:
                                            System.out.print("GAS: ");
                                            break;
                                        case 1:
                                            System.out.print("ELECTRICITY: ");
                                            break;
                                        case 2:
                                            System.out.print("WATER: ");
                                            break;
                                        case 3:
                                            System.out.print("PHONE: ");
                                            break;
                                        case 4:
                                            System.out.print("INTERNET: ");
                                            break;
                                    }
                                    CBS[sector][sub][st][house][portion].newUnits[i] = input.nextInt();
                                    input.nextLine();
                                    switch (i) {
                                        case 0:
                                            CBS[sector][sub][st][house][portion].billAmount[i] = gasBill(
                                                    CBS[sector][sub][st][house][portion].prevUnits[i],
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 1:
                                            CBS[sector][sub][st][house][portion].billAmount[i] = electricityBill(
                                                    CBS[sector][sub][st][house][portion].prevUnits[i],
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 2:
                                            CBS[sector][sub][st][house][portion].billAmount[i] = waterBill(
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 3:
                                            CBS[sector][sub][st][house][portion].billAmount[i] = phoneBill(
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 4:
                                            CBS[sector][sub][st][house][portion].billAmount[i] = internetBill(
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                    }
                                }

                                BufferedReader aikAurReader = new BufferedReader(new FileReader(file));
                                PrintWriter modifier = new PrintWriter(new BufferedWriter(new FileWriter(
                                        "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Temp.txt")));
                                String line;
                                while ((line = aikAurReader.readLine()) != null) {
                                    if (modify.equalsIgnoreCase(line)) {
                                        aikAurReader.readLine();
                                        modifier.println(CBS[sector][sub][st][house][portion].cID);
                                        modifier.println(CBS[sector][sub][st][house][portion].name);
                                    } else {
                                        modifier.println(line);
                                    }
                                }
                                aikAurReader.close();
                                modifier.flush();
                                modifier.close();
                                thisMonth.delete();
                                temp.renameTo(thisMonth);
                                System.out.println("Record modified successfully!");
                            }
                        }
                    }
                }
            }
        }
        showMenu();
    }

    // allows deletion of preexisting records from file of any month
    public static void deleteRecords() throws IOException {
        File temp = new File("C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Temp.txt");
        byte recordMonth;
        do {
            System.out.print(
                    "1:Jan   2:Feb   3:Mar  4:Apr   5:May   6:Jun\n7:Jul   8:Aug    9:Sep   10:Oct   11:Nov   12:Dec\nDelete record for month: ");
            recordMonth = input.nextByte();
        } while ((recordMonth < 1) || (recordMonth > 12));
        String file = monthChooser(recordMonth);
        File thisMonth = new File(file);
        int clientID;
        do {
            System.out.print("Enter client ID: ");
            clientID = input.nextInt();
        } while ((clientID < 100000) || (clientID > 124000));
        for (byte sector = 0; sector < CBS.length; sector++) {
            for (byte sub = 0; sub < CBS[sector].length; sub++) {
                for (byte st = 0; st < CBS[sector][sub].length; st++) {
                    for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                        for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                            if (clientID == CBS[sector][sub][st][house][portion].cID) {
                                System.out.println("EXISTING RECORD:");
                                System.out.println("Client ID: " + CBS[sector][sub][st][house][portion].cID);
                                System.out.println("Name: " + CBS[sector][sub][st][house][portion].name + "\n");
                                CBS[sector][sub][st][house][portion].name = null;
                                PrintWriter deleter = new PrintWriter(new BufferedWriter(new FileWriter(
                                        "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Temp.txt", true)));
                                BufferedReader br = new BufferedReader(new FileReader(thisMonth));
                                String delete = CBS[sector][sub][st][house][portion].cID + "";
                                String record;
                                while ((record = br.readLine()) != null) {
                                    if (delete.equalsIgnoreCase(record)) {
                                        for (int i = 1; i <= 6; i++) {
                                            br.readLine();
                                        }
                                    } else {
                                        deleter.println(record);
                                    }
                                }
                                deleter.flush();
                                deleter.close();
                                br.close();
                                thisMonth.delete();
                                temp.renameTo(thisMonth);
                                System.out.println("Record deleted successfully!");
                                break;
                            }
                        }
                    }
                }
            }
        }
        showMenu();
    }

    public static void searchRecords() throws IOException {
        int clientID;
        do {
            System.out.print("Enter client ID: ");
            clientID = input.nextInt();
        } while ((clientID < 100000) || (clientID > 124000));
        displayRecord(clientID);
        showMenu();
    }

    public static void displayRecord(int clientID) throws IOException {
        int recordMonth;
        do {
            System.out.print(
                    "1:Jan   2:Feb   3:Mar  4:Apr   5:May   6:Jun\n7:Jul   8:Aug    9:Sep   10:Oct   11:Nov   12:Dec\nDisplay record for month: ");
            recordMonth = input.nextByte();
            input.nextLine();
        } while ((recordMonth < 1) || (recordMonth > 12));
        String file = monthChooser(recordMonth);

        for (byte sector = 0; sector < CBS.length; sector++) {
            for (byte sub = 0; sub < CBS[sector].length; sub++) {
                for (byte st = 0; st < CBS[sector][sub].length; st++) {
                    for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                        for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                            if (clientID == CBS[sector][sub][st][house][portion].cID) {
                                String search = CBS[sector][sub][st][house][portion].cID + "";
                                String record;
                                BufferedReader br = new BufferedReader(new FileReader(file));
                                while ((record = br.readLine()) != null) {
                                    if (search.equalsIgnoreCase(record)) {
                                        System.out.println("Client ID: " + record);
                                        System.out.println("Name: " + br.readLine());
                                        for (int i = 1; i <= 5; i++) {
                                            StringTokenizer s = new StringTokenizer(br.readLine(), "|");
                                            s.nextToken();
                                            switch (i) {
                                                case 1:
                                                    System.out.printf("%-12s||", "GAS");
                                                    break;
                                                case 2:
                                                    System.out.printf("%-12s||", "ELECTRICITY");
                                                    break;
                                                case 3:
                                                    System.out.printf("%-12s||", "WATER");
                                                    break;
                                                case 4:
                                                    System.out.printf("%-12s||", "PHONE");
                                                    break;
                                                case 5:
                                                    System.out.printf("%-12s||", "INTERNET");
                                                    break;
                                            }
                                            System.out.println("Previous reading: " + s.nextToken() + " New reading: "
                                                    + s.nextToken() + " Bill amount: " + s.nextToken());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        showMenu();
    }

    // writes the input taken from keyboard to Jan2022 file
    public static void writeRecords(String fileName, String client, int clientID, int[] prev, int[] current,
            double[] bill) throws IOException {
        PrintWriter righter = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
        righter.println(clientID);
        righter.println(client);
        for (int i = 0; i <= 4; i++) {
            switch (i) {
                case 0:
                    righter.print("Gas |");
                    break;
                case 1:
                    righter.print("\nElec |");
                    break;
                case 2:
                    righter.print("\nWater |");
                    break;
                case 3:
                    righter.print("\nPhone |");
                    break;
                case 4:
                    righter.print("\nInt |");
                    break;
            }
            righter.printf("%d|%d|%.1f|", prev[i], current[i], bill[i]);
        }
        righter.println();
        righter.flush();
        righter.close();
    }

    public static double gasBill(int prevRead, int newRead) {
        double units = (newRead - prevRead) / 100.0;
        double mmbtu = ((units * 1056) / 281.7385);
        double meterRent = 40.0;
        if (units <= 0.5) {
            return (mmbtu * 121) + meterRent;
        } else if (units <= 1.00) {
            return mmbtu * 300 + meterRent;
        } else if (units <= 2.00) {
            return mmbtu * 553 + meterRent;
        } else if (units <= 3.00) {
            return mmbtu * 738 + meterRent;
        } else if (units <= 4.00) {
            return mmbtu * 1107 + meterRent;
        } else {
            return mmbtu * 1460 + meterRent;
        }
    }

    public static double electricityBill(int prevRead, int newRead) {
        int units = newRead - prevRead;
        if (units < 100) {
            return units * 10;
        } else if (units <= 199) {
            return (units - 100) * 15 + 1000;
        } else if (units <= 300) {
            return (units - 200) * 18 + 3000;
        } else {
            return units * 25;
        }
    }

    public static double waterBill(int liters) {
        if (liters < 1000) {
            return 400;
        } else if (liters <= 2000) {
            return 1000;
        } else {
            return ((1.5 * liters) + 1000);
        }
    }

    public static double phoneBill(int minutes) {
        int type = random.nextInt(2);
        if (type == 0) {
            return minutes * 5; // type 1: local calls
        } else {
            return minutes * 7; // type 2: international calls
        }
    }

    public static double internetBill(int gbs) {
        return gbs * 10;
    }

    public static void clientReport() throws IOException {
        double total = 0;
        int clientID;
        do {
            System.out.println("Enter client ID: ");
            clientID = input.nextInt();
        } while ((clientID < 100000) || (clientID > 124000));
        double[] totalBill = new double[5];
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("\t\t\t\t\t\t\t<< CLIENT REPORT >>");
        for (int i = 1; i <= 12; i++) {
            String file = monthChooser(i);
            System.out.println(file.toUpperCase()
                    .replace(Paths.get(System.getProperty("user.dir")).toString().toUpperCase() + "\\", "")
                    + "-------------------------------------------------------------------------");

            for (byte sector = 0; sector < CBS.length; sector++) {
                for (byte sub = 0; sub < CBS[sector].length; sub++) {
                    for (byte st = 0; st < CBS[sector][sub].length; st++) {
                        for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                            for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                                if (clientID == CBS[sector][sub][st][house][portion].cID) {
                                    String search = CBS[sector][sub][st][house][portion].cID + "";
                                    String record;
                                    BufferedReader br = new BufferedReader(new FileReader(file));
                                    while ((record = br.readLine()) != null) {
                                        if (search.equalsIgnoreCase(record)) {
                                            System.out.println("Client ID: " + record);
                                            System.out.println("Name: " + br.readLine());
                                            System.out.printf(
                                                    "Address: Sector %c, Sub-sector: %d, Street: %d, House# %d, Portion: %d%n",
                                                    (char) (sector + 65), sub, st, house, portion);
                                            System.out.println(
                                                    "--------------------------------------------------------------------------------");
                                            for (int j = 0; j < 5; j++) {
                                                StringTokenizer s = new StringTokenizer(br.readLine(), "|");
                                                s.nextToken();
                                                switch (j) {
                                                    case 0:
                                                        System.out.printf("%-12s||", "GAS");
                                                        break;
                                                    case 1:
                                                        System.out.printf("%-12s||", "ELECTRICITY");
                                                        break;
                                                    case 2:
                                                        System.out.printf("%-12s||", "WATER");
                                                        break;
                                                    case 3:
                                                        System.out.printf("%-12s||", "PHONE");
                                                        break;
                                                    case 4:
                                                        System.out.printf("%-12s||", "INTERNET");
                                                        break;
                                                }
                                                System.out.printf(" Previous reading: %3s%s%4s", s.nextToken(),
                                                        "\tNew Reading: ", s.nextToken());
                                                double bill = Double.parseDouble(s.nextToken());
                                                totalBill[j] += bill;
                                                System.out.printf("%s%.2f%n", "\tBill Amount: ", bill);
                                                total += totalBill[j];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-24s%.2f%n", "Total Gas Bill: ", totalBill[0]);
        System.out.printf("%-24s%.2f%n", "Total Electricity Bill: ", totalBill[1]);
        System.out.printf("%-24s%.2f%n", "Total Water Bill: ", totalBill[2]);
        System.out.printf("%-24s%.2f%n", "Total Phone Bill: ", totalBill[3]);
        System.out.printf("%-24s%.2f%n", "Total Internet Bill: ", totalBill[4]);
        System.out.printf("%-24s%.2f%n", "Total Bill: ", total);
        System.out.println("--------------------------------------------------------------------------------");
        showMenu();
    }

    public static void sectorReport() throws IOException {
        byte sector = 0;
        try {
            System.out.print(
                    "0:'A'    1:'B'   2:'C'   3:'D'   4:'E'\n5:'F'    6:'G'   7:'H'   8:'I'   9:'J'\nEnter Sector: ");
            sector = input.nextByte();
        } catch (Exception e) {
            System.out.println("Invalid Input");
            showMenu();
        }
        System.out.println("-------------------------------------");
        System.out.printf("\t\t\t<< SECTOR %s >>%n", (char) (sector + 65));
        for (int i = 1; i <= 12; i++) { // loop for months
            double[] totalBill = new double[5];
            int client = 0;
            String file = monthChooser(i);
            System.out.println(file.toUpperCase().replace("C:\\USERS\\HP\\IDEAPROJECTS\\COMBINEDBILLINGSYSTEM\\", "")
                    + "------------------------------");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                for (byte sub = 0; sub < CBS[sector].length; sub++) {
                    for (byte st = 0; st < CBS[sector][sub].length; st++) {
                        for (byte house = 0; house < CBS[sector][sub][house].length; house++) {
                            for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                                String id = CBS[sector][sub][st][house][portion].cID + "";
                                if (line.equalsIgnoreCase(id)) {
                                    client++;
                                    reader.readLine();
                                    for (int j = 0; j < 5; j++) {
                                        StringTokenizer s = new StringTokenizer(reader.readLine(), "|");
                                        s.nextToken();
                                        s.nextToken();
                                        s.nextToken();
                                        float bill = Float.parseFloat(s.nextToken());
                                        totalBill[j] += bill;

                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("No. of Clients: " + client);
            System.out.println("Total Gas Bill: " + totalBill[0]);
            System.out.println("Total Electricity Bill: " + totalBill[1]);
            System.out.println("Total Water Bill: " + totalBill[2]);
            System.out.println("Total Phone Bill: " + totalBill[3]);
            System.out.println("Total Internet Bill: " + totalBill[4]);
        }
        System.out.println("-------------------------------------");
        showMenu();
    }

    public static void subSectorReport() throws IOException {
        byte sub = 0, sector = 0;
        try {
            System.out.print(
                    "0:'A'    1:'B'   2:'C'   3:'D'   4:'E'\n5:'F'    6:'G'   7:'H'   8:'I'   9:'J'\nEnter Sector: ");
            sector = input.nextByte();
            System.out.print("OPTIONS: 0   1   2   3\nEnter Sub-Sector: ");
            sub = input.nextByte();
        } catch (Exception e) {
            System.out.println("Invalid Input");
            showMenu();
        }
        System.out.println("-------------------------------------");
        System.out.printf("\t<< SECTOR %s || SUBSECTOR %d >>%n", (char) (sector + 65), sub);
        for (int i = 1; i <= 12; i++) { // loop for months
            double[] totalBill = new double[5];
            int client = 0;
            String file = monthChooser(i);
            System.out.println(file.toUpperCase().replace("C:\\USERS\\HP\\IDEAPROJECTS\\COMBINEDBILLINGSYSTEM\\", "")
                    + "------------------------------");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                for (byte st = 0; st < CBS[sector][sub].length; st++) {
                    for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                        for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                            String id = CBS[sector][sub][st][house][portion].cID + "";
                            if (line.equalsIgnoreCase(id)) {
                                client++;
                                reader.readLine();
                                for (int j = 0; j < 5; j++) {
                                    StringTokenizer s = new StringTokenizer(reader.readLine(), "|");
                                    s.nextToken();
                                    s.nextToken();
                                    s.nextToken();
                                    float bill = Float.parseFloat(s.nextToken());
                                    totalBill[j] += bill;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("No. of Clients: " + client);
            System.out.println("Total Gas Bill: " + totalBill[0]);
            System.out.println("Total Electricity Bill: " + totalBill[1]);
            System.out.println("Total Water Bill: " + totalBill[2]);
            System.out.println("Total Phone Bill: " + totalBill[3]);
            System.out.println("Total Internet Bill: " + totalBill[4]);
        }
        System.out.println("-------------------------------------");
        showMenu();
    }

    public static void streetReport() throws IOException {
        byte sub = 0, sector = 0, st = 0;
        try {
            System.out.print(
                    "0:'A'    1:'B'   2:'C'   3:'D'   4:'E'\n5:'F'    6:'G'   7:'H'   8:'I'   9:'J'\nEnter Sector: ");
            sector = input.nextByte();
            System.out.print("OPTIONS: 0   1   2   3\nEnter Sub-Sector: ");
            sub = input.nextByte();
            System.out.print("OPTIONS: 0 1 2 3 4 5 6 7 8 9\nEnter Street: ");
            st = input.nextByte();
        } catch (Exception e) {
            System.out.println("Invalid Input");
            showMenu();
        }
        System.out.println("---------------------------------------------");
        System.out.printf("  << SECTOR %s || SUBSECTOR %d || STREET %d >>%n", (char) (sector + 65), sub, st);
        for (int i = 1; i <= 12; i++) { // loop for months
            double[] totalBill = new double[5];
            int client = 0;
            String file = monthChooser(i);
            System.out.println(file.toUpperCase().replace("C:\\USERS\\HP\\IDEAPROJECTS\\COMBINEDBILLINGSYSTEM\\", "")
                    + "--------------------------------------");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                    for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                        String id = CBS[sector][sub][st][house][portion].cID + "";
                        if (line.equalsIgnoreCase(id)) {
                            client++;
                            reader.readLine();
                            for (int j = 0; j < 5; j++) {
                                StringTokenizer s = new StringTokenizer(reader.readLine(), "|");
                                s.nextToken();
                                s.nextToken();
                                s.nextToken();
                                float bill = Float.parseFloat(s.nextToken());
                                totalBill[j] += bill;
                            }
                        }
                    }
                }
            }
            System.out.println("No. of Clients: " + client);
            System.out.println("Total Gas Bill: " + totalBill[0]);
            System.out.println("Total Electricity Bill: " + totalBill[1]);
            System.out.println("Total Water Bill: " + totalBill[2]);
            System.out.println("Total Phone Bill: " + totalBill[3]);
            System.out.println("Total Internet Bill: " + totalBill[4]);
        }
        System.out.println("---------------------------------------------");
        showMenu();
    }

    public static void houseReport() throws IOException {
        byte sub = 0, sector = 0, st = 0, house = 0;
        try {
            System.out.print(
                    "0:'A'    1:'B'   2:'C'   3:'D'   4:'E'\n5:'F'    6:'G'   7:'H'   8:'I'   9:'J'\nEnter Sector: ");
            sector = input.nextByte();
            System.out.print("OPTIONS: 0   1   2   3\nEnter Sub-Sector: ");
            sub = input.nextByte();
            System.out.print("OPTIONS: 0 1 2 3 4 5 6 7 8 9\nEnter Street: ");
            st = input.nextByte();
            System.out.print("OPTIONS: 0-19\nEnter House# : ");
            house = input.nextByte();
        } catch (Exception e) {
            System.out.println("Invalid Input");
            showMenu();
        }
        System.out.println("-----------------------------------------------------");
        System.out.printf("<< SECTOR %s || SUBSECTOR %d || STREET %d || HOUSE# %d >>%n", (char) (sector + 65), sub, st,
                house);
        for (int i = 1; i <= 12; i++) { // loop for months
            double[] totalBill = new double[5];
            int client = 0;
            String file = monthChooser(i);
            System.out.println(file.toUpperCase().replace("C:\\USERS\\HP\\IDEAPROJECTS\\COMBINEDBILLINGSYSTEM\\", "")
                    + "----------------------------------------------");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                    String id = CBS[sector][sub][st][house][portion].cID + "";
                    if (line.equalsIgnoreCase(id)) {
                        client++;
                        reader.readLine();
                        for (int j = 0; j < 5; j++) {
                            StringTokenizer s = new StringTokenizer(reader.readLine(), "|");
                            s.nextToken();
                            s.nextToken();
                            s.nextToken();
                            float bill = Float.parseFloat(s.nextToken());
                            totalBill[j] += bill;
                        }
                    }
                }
            }
            System.out.println("No. of Clients: " + client);
            System.out.println("Total Gas Bill: " + totalBill[0]);
            System.out.println("Total Electricity Bill: " + totalBill[1]);
            System.out.println("Total Water Bill: " + totalBill[2]);
            System.out.println("Total Phone Bill: " + totalBill[3]);
            System.out.println("Total Internet Bill: " + totalBill[4]);
        }
        System.out.println("-----------------------------------------------------");
        showMenu();
    }

    public static void monthlyReport() throws IOException {
        int recordMonth, clientNo = 0;
        do {
            System.out.print(
                    "1:Jan   2:Feb   3:Mar  4:Apr   5:May   6:Jun\n7:Jul   8:Aug    9:Sep   10:Oct   11:Nov   12:Dec\nDisplay record for month: ");
            recordMonth = input.nextByte();
            input.nextLine();
        } while ((recordMonth < 1) || (recordMonth > 12));
        String file = monthChooser(recordMonth);
        BufferedReader br = new BufferedReader(new FileReader(file));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("\t\t\t<< "
                + file.toUpperCase().replace("C:\\USERS\\HP\\IDEAPROJECTS\\COMBINEDBILLINGSYSTEM\\", "") + " >>");
        double[] totalBill = new double[5];
        String record;
        while ((record = br.readLine()) != null) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Client ID: " + record);
            clientNo++;
            System.out.println("Name: " + br.readLine());
            System.out.println("--------------------------------------------------------------------------------");
            for (int j = 0; j < 5; j++) {
                StringTokenizer s = new StringTokenizer(br.readLine(), "|");
                s.nextToken();
                switch (j) {
                    case 0:
                        System.out.printf("%-12s||", "GAS");
                        break;
                    case 1:
                        System.out.printf("%-12s||", "ELECTRICITY");
                        break;
                    case 2:
                        System.out.printf("%-12s||", "WATER");
                        break;
                    case 3:
                        System.out.printf("%-12s||", "PHONE");
                        break;
                    case 4:
                        System.out.printf("%-12s||", "INTERNET");
                        break;
                }
                System.out.printf(" Previous reading: %3s%s%4s", s.nextToken(), "\tNew Reading: ", s.nextToken());
                double bill = Double.parseDouble(s.nextToken());
                totalBill[j] += bill;
                System.out.printf("%s%.2f%n", "\tBill Amount: ", bill);
            }
        }
        System.out.println("----------------------------------SUMMARY---------------------------------------");
        System.out.println("Total Number of Clients: " + clientNo);
        System.out.printf("%-24s%.2f%n", "Total Gas Bill: ", totalBill[0]);
        System.out.printf("%-24s%.2f%n", "Total Electricity Bill: ", totalBill[1]);
        System.out.printf("%-24s%.2f%n", "Total Water Bill: ", totalBill[2]);
        System.out.printf("%-24s%.2f%n", "Total Phone Bill: ", totalBill[3]);
        System.out.printf("%-24s%.2f%n", "Total Internet Bill: ", totalBill[4]);
        System.out.println("--------------------------------------------------------------------------------");
        showMenu();
    }

    public static void generateFiles() throws IOException {
        for (int j = 2; j <= 12; j++) {
            String month = monthChooser(j);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(month)));
            for (byte sector = 0; sector < CBS.length; sector++) {
                for (byte sub = 0; sub < CBS[sector].length; sub++) {
                    for (byte st = 0; st < CBS[sector][sub].length; st++) {
                        for (byte house = 0; house < CBS[sector][sub][st].length; house++) {
                            for (byte portion = 0; portion < CBS[sector][sub][st][house].length; portion++) {
                                pw.println(CBS[sector][sub][st][house][portion].cID);
                                pw.println(CBS[sector][sub][st][house][portion].name);
                                for (int i = 0; i < 5; i++) {
                                    CBS[sector][sub][st][house][portion].prevUnits[i] = CBS[sector][sub][st][house][portion].newUnits[i];
                                    switch (i) {
                                        case 0:
                                            CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(200);
                                            CBS[sector][sub][st][house][portion].billAmount[i] = gasBill(0,
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 1:
                                            CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(500);
                                            CBS[sector][sub][st][house][portion].billAmount[i] = electricityBill(0,
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 2:
                                            CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(3000);
                                            CBS[sector][sub][st][house][portion].billAmount[i] = waterBill(
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 3:
                                            CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(500);
                                            CBS[sector][sub][st][house][portion].billAmount[i] = phoneBill(
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                        case 4:
                                            CBS[sector][sub][st][house][portion].newUnits[i] = random.nextInt(500);
                                            CBS[sector][sub][st][house][portion].billAmount[i] = internetBill(
                                                    CBS[sector][sub][st][house][portion].newUnits[i]);
                                            break;
                                    }
                                }
                                writeRecords(month, CBS[sector][sub][st][house][portion].name,
                                        CBS[sector][sub][st][house][portion].cID,
                                        CBS[sector][sub][st][house][portion].prevUnits,
                                        CBS[sector][sub][st][house][portion].newUnits,
                                        CBS[sector][sub][st][house][portion].billAmount);
                            }
                        }
                    }
                }
            }
        }
    }

    public static String monthChooser(int month) {
        String monthFile = null;
        switch (month) {
            case 1:
                monthFile = Paths.get(System.getProperty("user.dir"), "Jan2022").toString();
                System.out.println(monthFile);
                break;
            case 2:
                monthFile = Paths.get(System.getProperty("user.dir"), "Feb2022").toString();
                break;
            case 3:
                monthFile = Paths.get(System.getProperty("user.dir"), "Mar2022").toString();
                break;
            case 4:
                monthFile = Paths.get(System.getProperty("user.dir"), "Apr2022").toString();
                break;
            case 5:
                monthFile = Paths.get(System.getProperty("user.dir"), "May2022").toString();
                break;
            case 6:
                monthFile = Paths.get(System.getProperty("user.dir"), "Jun2022").toString();
                break;
            case 7:
                monthFile = Paths.get(System.getProperty("user.dir"), "Jul2022").toString();
                break;
            case 8:
                monthFile = Paths.get(System.getProperty("user.dir"), "Aug2022").toString();
                break;
            case 9:
                monthFile = Paths.get(System.getProperty("user.dir"), "Sep2022").toString();
                break;
            case 10:
                monthFile = Paths.get(System.getProperty("user.dir"), "Oct2022").toString();
                break;
            case 11:
                monthFile = Paths.get(System.getProperty("user.dir"), "Nov2022").toString();
                break;
            case 12:
                monthFile = Paths.get(System.getProperty("user.dir"), "Dec2022").toString();
                break;
        }
        return monthFile;
    }
}

/*
 * case 2:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Feb2022";
 * break;
 * case 3:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Mar2022";
 * break;
 * case 4:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Apr2022";
 * break;
 * case 5:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\May2022";
 * break;
 * case 6:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Jun2022";
 * break;
 * case 7:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Jul2022";
 * break;
 * case 8:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Aug2022";
 * break;
 * case 9:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Sep2022";
 * break;
 * case 10:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Oct2022";
 * break;
 * case 11:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Nov2022";
 * break;
 * case 12:
 * monthFile = "C:\\Users\\HP\\IdeaProjects\\CombinedBillingSystem\\Dec2022";
 * break;
 */
