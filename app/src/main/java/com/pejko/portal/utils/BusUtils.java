package com.pejko.portal.utils;

import com.pejko.portal.entity.ModelBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class BusUtils {

    private static Calendar calStart = Calendar.getInstance();
    private static Calendar calEnd = Calendar.getInstance();
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    /**
     * kosak = 1
     * 6 = 2
     * Kriz = 3
     * 10 = 4
     * 13 = 5
     * 20 = 6
     * 22 = 7
     * 29 = 8
     * 38 = 9
     * 40 = 10
     */
    public static boolean canAddBus(ModelBus bus, Calendar calNow) {
        String symbol = bus.getSymbol();
        if (symbol.equalsIgnoreCase("1|7")) {
            if (isState1(calNow) && isState7(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("1|2|6")) {
            if ((isState1(calNow) || isState7(calNow)) && isState6(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("3")) {
            if (isState3(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("1|4")) {
            if (isState1(calNow) && isState4(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("2|6")) {
            if (isState2(calNow) && isState6(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("3|8")) {
            if (isState3(calNow) && isState6(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("1|9")) {
            if (isState1(calNow) || isState9(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("2|3")) {
            if (isState2(calNow) || isState3(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("10")) {
            if (isState10(calNow)) {
                return true;
            }
        } else if (symbol.equalsIgnoreCase("3|5")) {
            if (isState3(calNow) && isState5(calNow)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isState1(Calendar calNow) {
        return calNow.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && calNow.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY;
    }

    private static boolean isState2(Calendar calNow) {
        return calNow.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }

    private static boolean isState3(Calendar calNow) {
        return calNow.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || isHoliday(calNow);
    }

    private static boolean isState4(Calendar calNow) {
        return !isTimeBetweenTimes(calNow, "23.12.2015", "07.01.2016") &&
                !isTimeBetweenTimes(calNow, "01.02.2016", "01.02.2016") &&
                !isTimeBetweenTimes(calNow, "29.02.2016", "04.03.2016") &&
                !isTimeBetweenTimes(calNow, "24.03.2016", "29.03.2016") &&
                !isTimeBetweenTimes(calNow, "01.07.2016", "02.09.2016") &&
                !isTimeBetweenTimes(calNow, "28.10.2016", "31.10.2016");
    }

    private static boolean isState5(Calendar calNow) {
        return !isTimeBetweenTimes(calNow, "24.12.2015", "26.12.2015");
    }

    private static boolean isState6(Calendar calNow) {
        return !isTimeBetweenTimes(calNow, "26.12.2015", "26.12.2015");
    }

    private static boolean isState7(Calendar calNow) {
        return !isTimeBetweenTimes(calNow, "31.12.2015", "31.12.2015");
    }

    private static boolean isState8(Calendar calNow) {
        return !isTimeBetweenTimes(calNow, "24.12.2015", "24.12.2015") &&
                !isTimeBetweenTimes(calNow, "25.03.2016", "25.03.2016") &&
                !isTimeBetweenTimes(calNow, "05.07.2016", "05.07.2016") &&
                !isTimeBetweenTimes(calNow, "29.08.2016", "01.09.2016") &&
                !isTimeBetweenTimes(calNow, "15.09.2016", "15.09.2016") &&
                !isTimeBetweenTimes(calNow, "17.11.2016", "17.11.2016");
    }

    private static boolean isState9(Calendar calNow) {
        return isTimeBetweenTimes(calNow, "23.12.2015", "30.12.2015") ||
                isTimeBetweenTimes(calNow, "04.01.2016", "07.01.2016") ||
                isTimeBetweenTimes(calNow, "01.02.2016", "01.02.2016") ||
                isTimeBetweenTimes(calNow, "29.02.2016", "04.03.2016") ||
                isTimeBetweenTimes(calNow, "24.03.2016", "29.03.2016") ||
                isTimeBetweenTimes(calNow, "01.07.2016", "02.09.2016") ||
                isTimeBetweenTimes(calNow, "28.10.2016", "31.10.2016");
    }

    private static boolean isState10(Calendar calNow) {
        return isTimeBetweenTimes(calNow, "02.01.2016", "02.01.2016") ||
                isTimeBetweenTimes(calNow, "06.02.2016", "06.02.2016") ||
                isTimeBetweenTimes(calNow, "05.03.2016", "05.03.2016") ||
                isTimeBetweenTimes(calNow, "02.04.2016", "02.04.2016") ||
                isTimeBetweenTimes(calNow, "07.05.2016", "07.05.2016") ||
                isTimeBetweenTimes(calNow, "04.06.2016", "04.06.2016") ||
                isTimeBetweenTimes(calNow, "02.07.2016", "02.07.2016") ||
                isTimeBetweenTimes(calNow, "06.08.2016", "06.08.2016") ||
                isTimeBetweenTimes(calNow, "03.09.2016", "03.09.2016") ||
                isTimeBetweenTimes(calNow, "01.10.2016", "01.10.2016") ||
                isTimeBetweenTimes(calNow, "05.11.2016", "05.11.2016") ||
                isTimeBetweenTimes(calNow, "03.12.2016", "03.12.2016");
    }

    private static boolean isTimeBetweenTimes(Calendar calNow, String start, String end) {
        try {
            Date dtStart = dateFormatter.parse(start);
            Date dtEnd = dateFormatter.parse(end);
            calStart.setTime(dtStart);
            calStart.set(Calendar.HOUR_OF_DAY, 0);
            calStart.set(Calendar.MINUTE, 0);
            calStart.set(Calendar.SECOND, 0);
            calEnd.setTime(dtEnd);
            calEnd.set(Calendar.HOUR_OF_DAY, 23);
            calEnd.set(Calendar.MINUTE, 59);
            calEnd.set(Calendar.SECOND, 59);
            return (calNow.after(calStart) && calNow.before(calEnd));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isHoliday(Calendar calNow) {
        return isTimeBetweenTimes(calNow, "01.01.2016", "01.01.2016")||
                isTimeBetweenTimes(calNow, "06.01.2016", "06.01.2016")||
                isTimeBetweenTimes(calNow, "25.03.2016", "25.03.2016")||
                isTimeBetweenTimes(calNow, "28.03.2016", "28.03.2016")||
                isTimeBetweenTimes(calNow, "01.05.2016", "01.05.2016")||
                isTimeBetweenTimes(calNow, "08.05.2016", "08.05.2016")||
                isTimeBetweenTimes(calNow, "05.07.2016", "05.07.2016")||
                isTimeBetweenTimes(calNow, "29.08.2016", "29.08.2016")||
                isTimeBetweenTimes(calNow, "01.09.2016", "01.09.2016")||
                isTimeBetweenTimes(calNow, "15.09.2016", "15.09.2016")||
                isTimeBetweenTimes(calNow, "01.11.2016", "01.11.2016")||
                isTimeBetweenTimes(calNow, "17.11.2016", "17.11.2016")||
                isTimeBetweenTimes(calNow, "24.12.2016", "24.12.2016")||
                isTimeBetweenTimes(calNow, "25.12.2016", "25.12.2016")||
                isTimeBetweenTimes(calNow, "26.12.2016", "26.12.2016");


    }
}
