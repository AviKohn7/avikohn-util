package dev.avikohn.util.testutil.comparestructures;

public class PrinterSettings{
    public static final Appendable DEFAULT_APPENDER = System.out;

    private final Appendable appender;
    private final boolean hideEmptyComparers;
    private final boolean filterPackages;
    public static final PrinterSettings DEFAULT = new PrinterSettings(DEFAULT_APPENDER, false, true);
    private PrinterSettings(Appendable appender, boolean hideEmptyComparers, boolean filterPackages){
        this.appender = appender;
        this.hideEmptyComparers = hideEmptyComparers;
        this.filterPackages = filterPackages;
    }
    public PrinterSettings withAppender(Appendable appender){
        return new PrinterSettings(appender, this.hideEmptyComparers, this.filterPackages);
    }
    public PrinterSettings withHideEmptyComparers(boolean hideEmptyComparers){
        return new PrinterSettings(this.appender, hideEmptyComparers, this.filterPackages);
    }
    public PrinterSettings withFilterPackages(boolean filterPackages){
        return new PrinterSettings(this.appender, this.hideEmptyComparers, filterPackages);
    }
    public Appendable getAppender(){
        return appender;
    }
    public boolean filterPackages(){
        return filterPackages;
    }
    public boolean hideEmptyComparers(){
        return hideEmptyComparers;
    }
}
