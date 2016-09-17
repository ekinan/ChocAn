import java.math.BigDecimal;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.MathContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 *
 * @author Enis
 * This class generates all four reports required in the requirements document,
 * and also does the weekly data processing for the data center.
 */
public class ReportGenerator
{
    /**
     * This method generates an individual member report using the information provided in args,
     * and it also takes in the database to look up the necessary information.
     * 
     * Note that this method should be called iff the member has at least one service record
     * that is within the reference date in args. Otherwise it will fail.
     * 
     * @param args Contains the information needed to generate the individual report
     * @param database Used to look up additional information associated with the record (e.g. service provider, service name).
     * 
     * @throws IOException 
     */
    public static void GenerateMemberReport(ReportParameters args, ChocAnDPS database) throws IOException
    {
        Utilities.SetInfoParameters("Member", "member", "to"); //Set the information to tell ReportGeneratorIntro that we're generating the intro of a member report.
        BufferedWriter writer = ReportGenerator.GenerateReportIntro(args,FileUtilities.MEMBER_LOC, Utilities.info); //First write the introduction of the report and the member information fields
        
        String tabOffset = "\t";
        
        ServiceRecord currentRecord = args.iterator.next(); //Now write the service records associated with that member.
        do
        {           
            writer.write(tabOffset+"Date of service: "+currentRecord.GetServiceDate().format(DateTimeFormatter.ofPattern(Format.DATE))+"\n");
            Provider servProvider = database.LookUpProvider(currentRecord.GetProviderNumber());
            writer.write(tabOffset+"Provider name: "+servProvider.GetName()+"\n");
            writer.write(tabOffset+"Service name: "+servProvider.LookUpService(currentRecord.GetServiceCode()).GetName()+"\n\n");
        } while (args.iterator.hasNext() && (Utilities.IsWithinDate((currentRecord = args.iterator.next()).GetCurrentDateTime(), args.refDate)));
        
        writer.close();
    }
    
    /**
     * This method generates an individual provider report using the information provided in args,
     * and it also takes in the database to look up the necessary information.
     * 
     * Note that this method should be called iff the provider has at least one service record
     * that is within the reference date in args. Otherwise it will fail.
     * 
     * 
     * 
     * @param args
     * @param database Used to look up additional information associated with the record
     * 
     * @return A two-tuple containing the total number of consultations and fees for the provider. This is returned
     * so as to facilitate concurrent generation of the EFT and summary reports during the DPS' weekly processing.
     * Note that these values are within their specified limits. If during the processing one of them is found to exceed
     * the limit (e.g. fee becomes greater than $99999.99), then it is brought back down to the limit before written
     * and returned.
     * @throws IOException 
     */
    public static AbstractMap.SimpleEntry<Integer, BigDecimal> GenerateProviderReport(ReportParameters args, ChocAnDPS database) throws IOException
    {
        Utilities.SetInfoParameters("Provider", "provider", "by");
        BufferedWriter writer = ReportGenerator.GenerateReportIntro(args, FileUtilities.PROVIDERS_LOC, Utilities.info); //Write the introduction of the report and the member information fields.
        
        String tabOffset = "\t";
        int totalConsultations = 0;
        BigDecimal totalFees = new BigDecimal("0");
        
        ServiceRecord currentRecord = args.iterator.next();    //Now write the services associated with that provider.     
        do
        {
            int memberNumber = currentRecord.GetMemberNumber();
            int serviceCode = currentRecord.GetServiceCode();
            int providerNumber = currentRecord.GetProviderNumber();
            BigDecimal fee = database.LookUpProvider(providerNumber).LookUpService(serviceCode).GetFee();
            writer.write(tabOffset+"Date of service: "+currentRecord.GetServiceDate().format(DateTimeFormatter.ofPattern(Format.DATE))+"\n");
            writer.write(tabOffset+"Date and time data were received by the computer: "+currentRecord.GetCurrentDateTime().format(DateTimeFormatter.ofPattern(Format.DATE+" "+Format.TIME))+"\n");
            writer.write(tabOffset+"Member name: "+database.LookUpMember(memberNumber).GetName()+"\n");
            writer.write(tabOffset+"Member code: "+memberNumber+"\n");
            writer.write(tabOffset+"Service code: "+serviceCode+"\n");
            writer.write(tabOffset+"Fee to be paid: "+Format.USD_COST_FORMAT.format(fee.doubleValue())+"\n\n");

            ++totalConsultations;
            totalFees = totalFees.add(fee);
        } while (args.iterator.hasNext() && (Utilities.IsWithinDate((currentRecord = args.iterator.next()).GetCurrentDateTime(), args.refDate)));
        
        totalConsultations = Utilities.AdjustIntToLimit(totalConsultations, Limits.MEMBER_CONSULTATIONS_LIMIT); //Make sure the consultations and fees are within the specified limits.   
        totalFees = Utilities.AdjustFeeToLimit(totalFees, Limits.WEEKLY_FEE_LIMIT);
        
        writer.write("Total number of consultations with members: "+totalConsultations+"\n");
        writer.write("Total fee for the week: "+Format.USD_COST_FORMAT.format(totalFees.doubleValue()));
        
        writer.close();
        
        return new AbstractMap.SimpleEntry<Integer, BigDecimal>(totalConsultations, totalFees);
    }
    
    /**
     * This method generates the EFT report corresponding to "database".
     * It contains all of the fields specified in the Requirements Document.
     * 
     * Note that this method is only called when a manager requests an EFT report.
     * The weekly ones are done concurrently with the provider reports. Also,
     * we only include the providers who have at least one service record
     * that is within the database's global date
     * 
     * @param database
     * @throws IOException 
     */
    public static void GenerateEFTReport(ChocAnDPS database) throws IOException
    {
        LocalDateTime globalDate = database.GetGlobalDate(); //Get the necessary data structures to do it
        TreeSet<Provider> providers = database.GetProviderDatabase();
        ServiceRecordDisk records = database.GetServiceRecordDisk();

        LocalDateTime startDate = globalDate.minusDays(Utilities.DayDifference(Parameters.WEEK_START,globalDate.getDayOfWeek()));
        String dateName = "Reports for "+Format.GetDateRange(startDate,startDate.plusDays(Parameters.WEEK_LENGTH)); //Get the "startDate" to "endDate" directory name
        String eftDir = FileUtilities.FILE_LOC+"/"+FileUtilities.EFT_LOC; //Get the EFT directory name
        
        FileUtilities.CreateDirectory(eftDir); //Create the EFT directory
        FileUtilities.CreateDirectory(eftDir+"/"+dateName); //Create the date directory
        
        BufferedWriter eftWriter = FileUtilities.OpenWriter(ReportGenerator.GetReportName(true, eftDir+"/"+dateName)); //Get the file name      
        
        Iterator<Provider> provIterator = providers.iterator();
        while (provIterator.hasNext()) //While there are providers, we keep writing
        {
            Provider prov = provIterator.next();
            ListIterator<ServiceRecord> iterator = records.StartAtProvider(prov.GetID());
            if (iterator.hasNext() && (Utilities.IsWithinDate(iterator.next().GetCurrentDateTime(), database.GetGlobalDate()))) //Provider does have a service record list for this week, so we can write to the EFT report
            {
                AbstractMap.SimpleEntry<Integer, BigDecimal> returnVals = ReportGenerator.ExtractRecordTotals(prov, records.StartAtProvider(prov.GetID()), database.GetGlobalDate());
                ReportGenerator.WriteEFTEntry(eftWriter, prov, returnVals.getValue());
            }
        }

        eftWriter.close();
    }
    
    /**
     * This method generates the summary report corresponding to "database".
     * It contains all of the fields specified in the Requirements Document.
     * 
     * Note that this method is only called when a manager requests a summary report.
     * The weekly ones are done concurrently with the provider reports. Also,
     * we only include the providers who have at least one service record
     * that is within the database's global date 
     * 
     * @param database
     * @throws IOException 
     */
    public static void GenerateSummaryReport(ChocAnDPS database) throws IOException
    {
        LocalDateTime globalDate = database.GetGlobalDate(); //Get the necessary data structures to do it
        TreeSet<Provider> providers = database.GetProviderDatabase();
        ServiceRecordDisk records = database.GetServiceRecordDisk();

        LocalDateTime startDate = globalDate.minusDays(Utilities.DayDifference(Parameters.WEEK_START,globalDate.getDayOfWeek()));
        String dateName = "Reports for "+Format.GetDateRange(startDate,startDate.plusDays(Parameters.WEEK_LENGTH));  //Get the "startDate" to "endDate" directory name
        String summaryDir = FileUtilities.FILE_LOC+"/"+FileUtilities.SUMMARY_LOC; //Get the summary directory name
        
        FileUtilities.CreateDirectory(summaryDir); //Create the summary directory
        FileUtilities.CreateDirectory(summaryDir+"/"+dateName); //Create the date directory
        
        BufferedWriter summaryWriter = FileUtilities.OpenWriter(ReportGenerator.GetReportName(true, summaryDir+"/"+dateName)); 
        ReportGenerator.WriteSummaryIntro(summaryWriter);
        
        int totalProviders = 0;
        int totalConsultations = 0;
        BigDecimal totalFees = new BigDecimal("0");
        
        Iterator<Provider> provIterator = providers.iterator();
        while (provIterator.hasNext())
        {
            Provider prov = provIterator.next();
            ListIterator<ServiceRecord> iterator = records.StartAtProvider(prov.GetID());
            if (iterator.hasNext() && (Utilities.IsWithinDate(iterator.next().GetCurrentDateTime(), database.GetGlobalDate()))) //Provider does have a service record list for this week, so we can write the summary report entry
            {
                ++totalProviders;
                AbstractMap.SimpleEntry<Integer, BigDecimal> returnVals = ReportGenerator.ExtractRecordTotals(prov, records.StartAtProvider(prov.GetID()), database.GetGlobalDate());
                ReportGenerator.WriteSummaryEntry(summaryWriter, prov, returnVals.getKey(), returnVals.getValue());
                totalConsultations += returnVals.getKey();
                totalFees = totalFees.add(returnVals.getValue());
            }
        }

        summaryWriter.write("Total number of providers who provided services: "+totalProviders+"\n");
        summaryWriter.write("Total consultations: "+totalConsultations+"\n");
        summaryWriter.write("Total fees: "+Format.USD_COST_FORMAT.format(totalFees.doubleValue())+"\n");        

        summaryWriter.close();        
    }
    
    /**
     * This method generates all four reports corresponding to the ChocAnDPS object database.
     * It does the weekly processing specified in the requirements document.
     * 
     * Note that to make the method more efficient, this generates the EFT and summary reports
     * using the data obtained from GenerateProviderReport instead of calling the individual
     * methods themselves.
     * 
     * After generating the reports, the method proceeds to filter out the service records
     * whose dates are within database.globalDate in order to avoid redundant services being
     * written after the week has ended.
     * 
     * Weekly processing is as follows.
     *      1) Generate all of the member reports.
     *      2) Generate all of the provider reports
     *          -After each provider report, update the EFT and summary report files to include
     *              the summary of this provider.
     *      3) Finish writing the EFT and summary reports
     *      4) Filter out the service records used to generate the four reports to avoid
     *              rewriting them in the future.
     * 
     * @param database
     * @throws IOException 
     */
    public static void GenerateWeeklyReports(ChocAnDPS database) throws IOException
    {
        LocalDateTime globalDate = database.GetGlobalDate(); //Get the necessary data structures.
        TreeSet<Member> members = database.GetMemberDatabase();
        TreeSet<Provider> providers = database.GetProviderDatabase();
        ServiceRecordDisk records = database.GetServiceRecordDisk();
        
        LocalDateTime startDate = globalDate.minusDays((int)Utilities.DayDifference(Parameters.WEEK_START,globalDate.getDayOfWeek()));
        String dateName = "Reports for "+Format.GetDateRange(startDate,startDate.plusDays(Parameters.WEEK_LENGTH)); //Get the date range directory name
        
        String eftDir = FileUtilities.FILE_LOC+"/"+FileUtilities.EFT_LOC; //Create the EFT and summary report directories here, as well as the corresponding date directories
        String summaryDir = FileUtilities.FILE_LOC+"/"+FileUtilities.SUMMARY_LOC;
        FileUtilities.CreateDirectory(eftDir);
        FileUtilities.CreateDirectory(summaryDir);
        FileUtilities.CreateDirectory(eftDir+"/"+dateName);        
        FileUtilities.CreateDirectory(summaryDir+"/"+dateName);      
      
        BufferedWriter eftWriter = FileUtilities.OpenWriter(ReportGenerator.GetReportName(false, eftDir+"/"+dateName)); //Open the file writes for the EFT and summary reports
        BufferedWriter summaryWriter = FileUtilities.OpenWriter(ReportGenerator.GetReportName(false,summaryDir+"/"+dateName));
        ReportGenerator.WriteSummaryIntro(summaryWriter); //Summary report has an introduction, EFT doesn't. So write it before writing anything else
        
        int totalProviders = 0;
        int totalConsultations = 0;
        BigDecimal totalFees = new BigDecimal("0");
        ReportParameters params = new ReportParameters(); //The report parameters. Use this object to avoid creating multiple ReportParameters objects
        
        //Generate member reports
        Iterator<Member> membIterator = members.iterator();
        while (membIterator.hasNext())
        {
            Member current = membIterator.next();
            ListIterator<ServiceRecord> recordStart = records.StartAtMember(current.GetID());
            if (recordStart.hasNext() && (Utilities.IsWithinDate(recordStart.next().GetCurrentDateTime(), database.GetGlobalDate()))) //Only generate report for members who have services for this week
            {
                params.SetParameters(current, records.StartAtMember(current.GetID()), globalDate, false);
                ReportGenerator.GenerateMemberReport(params,database);
            }
        }
        
        //Now generate provider reports, while concurrently updating EFT and Summary ones
        Iterator<Provider> provIterator = providers.iterator();
        while (provIterator.hasNext())
        {
            Provider current = provIterator.next();
            ListIterator<ServiceRecord> recordStart = records.StartAtProvider(current.GetID());
            if (recordStart.hasNext() && (Utilities.IsWithinDate(recordStart.next().GetCurrentDateTime(), database.GetGlobalDate()))) //Check if this provider has provided services for this week
            {
                ++totalProviders;
                params.SetParameters(current, records.StartAtProvider(current.GetID()), globalDate, false);
                AbstractMap.SimpleEntry<Integer, BigDecimal> returnVals = ReportGenerator.GenerateProviderReport(params,database);
                
                //After generating the provider report, write the corresponding EFT and summary entries.
                ReportGenerator.WriteEFTEntry(eftWriter, current, returnVals.getValue());
                ReportGenerator.WriteSummaryEntry(summaryWriter, current, returnVals.getKey(), returnVals.getValue());
            
                totalConsultations += returnVals.getKey().intValue();
                totalFees = totalFees.add(returnVals.getValue());
            }
        }
        
        summaryWriter.write("Total number of providers who provided services: "+totalProviders+"\n");        
        summaryWriter.write("Total consultations: "+totalConsultations+"\n");
        summaryWriter.write("Total fees: "+Format.USD_COST_FORMAT.format(totalFees.doubleValue())+"\n");
        
        eftWriter.close();
        summaryWriter.close();
        
        database.GetServiceRecordDisk().Filter(database.GetGlobalDate()); //Now filter the records.
    }
    
    /**
     * This method writes the information fields corresponding to identifier, where identifier is either "member" or "provider",
     * to the file stream pointed to by writer.
     * 
     * @param writer the open file stream
     * @param info the information object
     * @param identifier to classify if we're writing the information of a member or a provider.
     * @throws IOException 
     */
    public static void WriteInfo(BufferedWriter writer, final Information info, String identifier) throws IOException
    {
        writer.write(identifier+" name: "+info.GetName()+"\n");
        writer.write(identifier+" number: "+info.GetID()+"\n");
        writer.write(identifier+" street address: "+info.GetStreetAddress()+"\n");
        writer.write(identifier+" city: "+info.GetCity()+"\n");
        writer.write(identifier+" state: "+info.GetState()+"\n");
        writer.write(identifier+" zip code: "+info.GetZipCode()+"\n");
    }    
    
    /**
     * This method generates the first part of the report for members and providers. It writes the Information components (e.g. name, address)
     * and also gives a brief description of the file, whether it is manager generated or a result of the weekly processing, and
     * if it is a member report or provider report
     * 
     * @param args Report parameters
     * @param dirLoc Location of the directory for which to write the information in
     * @param info 0 = "Member" or "Provider," 1 = "member" or "provider, 2 = "to" or "by", respectively. Used to describe whether we're creating a member report or provider report
     * 
     * @return writer An open file stream so that the calling method can write the remaining parts of the report
     * @throws IOException 
     */
    private static BufferedWriter GenerateReportIntro(ReportParameters args, String dirLoc, String[] info) throws IOException
    {        
        LocalDateTime startDate = args.refDate.minusDays((int)Utilities.DayDifference(Parameters.WEEK_START, args.refDate.getDayOfWeek()));
        LocalDateTime endDate = startDate.plusDays(Parameters.WEEK_LENGTH);
        String reportDir = FileUtilities.FILE_LOC+"/"+dirLoc+"/"+args.info.GetID()+"/"+"Reports for "+Format.GetDateRange(startDate,endDate);
        FileUtilities.CreateDirectory(reportDir);
        
        BufferedWriter writer = FileUtilities.OpenWriter(ReportGenerator.GetReportName(args.isManagerReport,reportDir));
        ReportGenerator.WriteInfo(writer, args.info, info[0]);
        writer.write("\n");
        String typeOfRequest = (args.isManagerReport ? "manager requested" : "weekly");
        writer.write("Below is your "+typeOfRequest+" "+info[1]+" report of all services provided "+info[2]+" you for the\n");
        writer.write("dates of "+(Format.GetDateRange(startDate,args.refDate))+":\n\n");  
        
        return writer;
    }    
    
    /**
     * Returns the path of the report file. This is either some numerical valued file name if it is manager generated,
     * or it has the name "Main.txt" if it is a result of the weekly processing.
     * 
     * Note that manager reports have the numerical values because different managers could request the same member or
     * provider report several times during the week - the numerical values allows one to distinguish between newest
     * and oldest reports. For example if a report "1.txt" existed already and a manager requested another report of this
     * same kind, the new report would have the name "2.txt".
     * 
     * @param isManagerReport to see if it is a manager report or not
     * @param reportDir the directory in which the report file will be located
     * @return 
     */
    private static String GetReportName(final boolean isManagerReport, final String reportDir)
    {
        String reportPath = null;
        if (isManagerReport) //Get a file name if it is a manager report
        {
            int i = 0;
            File file = null;
            do
            {
                i=i+1;
                reportPath = reportDir+"/"+i+".txt";
                file = new File(reportDir+"/"+i+".txt");
            } while (file.exists());
        }
        else //Weekly generated, so the name is simply "Main.txt"
        {
            reportPath = reportDir+"/"+"Main"+".txt";
        }      
        
        return reportPath;
    }
    
    /**
     * This method writes the summary report introduction to the file stream referenced by writer.
     * 
     * @param writer
     * @throws IOException 
     */
    private static void WriteSummaryIntro(BufferedWriter writer) throws IOException
    {
        writer.write("This is the summary report. Below you will find all of the providers that need to be\n");
        writer.write("paid for that week, the number of consultations that each of them had, and the total\n");
        writer.write("amount that needs to be paid to them.\n\n");
    }
    
    /**
     * This method writes an individual entry in the EFT report
     * 
     * @param writer file to write to
     * @param prov provider that we're writing the EFT entry about
     * @param transferAmt the total amount of money that needs to be transferred
     * @throws IOException 
     */
    private static void WriteEFTEntry(BufferedWriter writer, Provider prov, BigDecimal transferAmt) throws IOException
    {
        writer.write("Provider name: "+prov.GetName()+"\n");
        writer.write("Provider number: "+prov.GetID()+"\n");
        writer.write("Amount to be transferred: "+Format.USD_COST_FORMAT.format(transferAmt.doubleValue())+"\n\n");
    }
    
    /**
     * This method writes an individual entry in the summary report
     * 
     * @param writer file to write to
     * @param prov provider that we're writing the summary about
     * @param totalConsultations total consultations for the provider
     * @param totalFee total fees that are owed
     * @throws IOException 
     */
    private static void WriteSummaryEntry(BufferedWriter writer, Provider prov, int totalConsultations, BigDecimal totalFee) throws IOException
    {
        writer.write("Provider name: "+prov.GetName()+"\n");
        writer.write("Number of consultations: "+totalConsultations+"\n");
        writer.write("Overall fee total: "+Format.USD_COST_FORMAT.format(totalFee.doubleValue())+"\n\n");
    }
    

    /**
     * This method extracts the total consultations and total fees of the provider from all of the records that are within refDate.
     * Note that this assumes the provider has at least one record that satisfies the above criteria.
     * 
     * @param prov The provider whose totals we're extracting
     * @param records The records associated with that provider
     * @param refDate The reference date with which to compare to
     * @return A (Integer, BIgDecial) tuple containing the totalConsltations and totalFees for the provider
     */
    private static AbstractMap.SimpleEntry<Integer, BigDecimal> ExtractRecordTotals(Provider prov, ListIterator<ServiceRecord> records, LocalDateTime refDate)
    {
        int totalConsultations = 0;
        BigDecimal totalFees = new BigDecimal("0");
        
        ServiceRecord curRecord = records.next();
        do
        {
            ++totalConsultations;
            totalFees = totalFees.add(prov.LookUpService(curRecord.GetServiceCode()).GetFee());
        } while (records.hasNext() && Utilities.IsWithinDate((curRecord = records.next()).GetCurrentDateTime(), refDate));
        
        return new AbstractMap.SimpleEntry<Integer, BigDecimal>(totalConsultations, totalFees);
    }
}
