package weymeelspierre.starstracker.library;

import android.text.format.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Pierre on 24/12/2014.
 */
public class Time_lib {

  /**
   * (imcce)
   * Temps légal (ou Heure légale):
   * Temps utilisé sur tout le territoire d´un pays donné. Il est décidé par les autorités
   * administratives qui choisissent, en général, d´adopter le Temps universel coordonné UTC décalé
   * d´un nombre entier d´heures. Par exemple, en France, pour l´année 2000, le temps légal est
   * UTC + 1 heure en hiver (heure d´hiver) et UTC + 2 heures en été (heure d´été).
   * <p/>
   * Temps propre:
   * En mécanique relativiste, le temps lu sur une horloge dans un laboratoire.
   * Il est différent du temps coordonnée.
   * <p/>
   * Temps sidéral en un lieu donné, à un instant donné:
   * Angle horaire de l´équinoxe. On parle du temps sidéral vrai lorsqu´il s´agit de l´équinoxe vrai
   * et du temps sidéral moyen lorsqu´il s´agit de l´équinoxe moyen de la date. En un lieu donné,
   * à un instant donné, la somme de l´ascension droite vraie d´un astre et de son angle horaire
   * est égale au temps sidéral vrai. Au moment du passage supérieur d´un astre au méridien,
   * son ascension droite vraie est donc égale au temps sidéral vrai.
   * <p/>
   * Temps solaire moyen:
   * Temps solaire vrai corrigé des inégalités de l´ascension droite du Soleil: c´est donc la partie
   * linéaire, par rapport au temps, du temps solaire vrai.
   * <p/>
   * Temps solaire vrai en un lieu, à un instant donné:
   * Angle horaire du centre du Soleil en ce lieu, à cet instant.
   * <p/>
   * Temps terrestre (TT):
   * Échelle de temps utilisée pour les éphémérides géocentriques apparentes dont l´unité de temps est
   * la seconde SI. Au 1 janvier 1977 0 h TAI, TT a pour valeur 1 janvier 1977, 0 h 0 min 32.184 s.
   * C´est une échelle de temps idéale dont la réalisation pratique est liée au Temps atomique
   * international TAI, par TT = TAI + 32.184 s.
   * <p/>
   * Temps universel (TU ou UT):
   * Échelle de temps étroitement liée à la rotation diurne de la Terre qui a longtemps été à la base
   * des temps légaux. TU est défini par une relation mathématique donnant l´expression du temps
   * sidéral en fonction du Temps universel. On peut donc déterminer TU à partir d´observations
   * d´étoiles (passage d´étoiles au méridien, par exemple). Le Temps universel ainsi obtenu est
   * rapporté à un pôle fixe sur la Terre et est noté UT0. Le Temps universel rapporté au pôle
   * céleste des éphémérides CEP s´obtient en s´affranchissant du mouvement du pôle et est noté UT1.
   * Depuis 1984 l´échelle de temps légale n´est plus basée sur le Temps universel mais sur le Temps
   * universel coordonné UTC.
   * <p/>
   * Temps universel coordonné (UTC):
   * Échelle de temps diffusée par les signaux horaires et utilisée comme base des temps légaux.
   * C´est, en fait le Temps atomique international TAI décalé d´un nombre entier de secondes.
   * Ce nombre est modifié régulièrement de telle sorte que la différence entre UTC et le Temps
   * universel UT1 n´excède pas 0.9 s en valeur absolue.
   * <p/>
   * Par convention internationale, le temps universel est le temps moyen de Greenwich,
   * augmenté de 12 heures.
   */
  private String dico;

  //USEFUL FOR EPHEMERIS:

  //ATTENTION en TT => UTC = TT - 1m 7s , TO DO
  public static Calendar getCurrentCalendarUTC() {
    SimpleDateFormat df = new SimpleDateFormat();
    df.setTimeZone(TimeZone.getTimeZone("UTC"));
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    return cal;
  }

  public static Calendar calendarCopy(Calendar cal) throws Exception {
    Calendar copy = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    copy.set(Calendar.YEAR, cal.get(Calendar.YEAR));
    copy.set(Calendar.MONTH, cal.get(Calendar.MONTH));
    copy.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
    setHHmmssSSS_calendar(copy, cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND),
            cal.get(Calendar.MILLISECOND));
    return copy;
  }

  public static void setDDMMYYYY_calendar(Calendar cal, int DD, int MM, int YYYY)
          throws Exception {
    cal.set(Calendar.DAY_OF_MONTH, DD);
    cal.set(Calendar.MONTH, MM);
    cal.set(Calendar.YEAR, YYYY);
  }

  public static void setHHmmssSSS_calendar(Calendar cal, int HH, int mm, int ss, int SSS)
          throws Exception {
    cal.set(Calendar.HOUR_OF_DAY, HH);
    cal.set(Calendar.MINUTE, mm);
    cal.set(Calendar.SECOND, ss);
    cal.set(Calendar.MILLISECOND, SSS);
  }
  //---------------------------------------------------------------------------------------

  //USEFUL FOR GEOELEMENTS:----------------------------------------------------------------

  /**
   * Valable slm pour le calendrier grégorien (=>à partir deu 15/10/1582)
   * (pour les dates antérieures, B=0)
   * calendar have to be an julian calendar per default...
   * (cf. p24 "Calculs astronomiques")
   * @return
   */
  public static double getJulianDay(Calendar calUTC,Boolean atMidnight) throws Exception {
    int year = calUTC.get(Calendar.YEAR);
    int month = calUTC.get(Calendar.MONTH)+1;//janvier est égal à 0 dans calendar !!;
    if(month <= 2){
      year = year - 1;
      month = month +12;
    }
    double decimalDay;
    if(atMidnight)
      decimalDay = calUTC.get(Calendar.DAY_OF_MONTH);
    else
      decimalDay = getDecimalDay(calUTC);
    double A = Math_lib.integerPart(year/100.0);
    double B = 2.0 - A + Math_lib.integerPart(A/4.0);
    double result= Math_lib.integerPart(365.25*(year+4716)) + Math_lib.integerPart(30.6001*(month+1))
            + decimalDay + B - 1524.5;
    String test ="ok";
    return result;

  }

  private static double getDecimalDay(Calendar cal) throws Exception{
    return (cal.get(Calendar.DAY_OF_MONTH)*86400.0 + cal.get(Calendar.HOUR_OF_DAY)*3600.0 +
            cal.get(Calendar.MINUTE)*60.0 + cal.get(Calendar.SECOND))/86400.0;
  }

  /**
   * Valable Seulement avec JJ atMidnight
   * cf. JM Calculs Astronomiques p35
   *
   * @param JJ = jour Julien correspondant à date courante et à l'heure courante de Greenwich
   * @return Temps sidéral à Greenwich.   (en degrés et décimales)
   */
  public static double sideralTimeInJulianCenturyUnity(double JJ) {
    return (JJ - 2451545.0) / 36525.0;
  }

  /**
   * sideral time at Greenwich location in degrees modulo 360
   * @param jcu_stForMidnightJD
   * @param seconOfDayUTC
   * @return
   */
  public static double getMeanGreenwichSideralTimeInDegrees(
          double jcu_stForMidnightJD,int seconOfDayUTC){
    double result = ((100.46061837 + 36000.770053608 * jcu_stForMidnightJD
            + 0.000387933 * Math.pow(jcu_stForMidnightJD, 2.0)
            + Math.pow(jcu_stForMidnightJD, 3.0) / 38710000)% 360.0)/15.0
            + (1.00273790935*seconOfDayUTC/3600.0) ;//heure et decimale et pas en seconOfDayUTC ?
    return result;
  }

  /**
   *ATTENTION getCurrentCalendarUTC donne TT et non UTC = TT - 1m 7s lors du test
   * qui à fourni une valeur de 1m 7s tros grande en temps sidéral ! TODO
   * longitude (en degrées décimales) doit être < 0 si de type "Est" !
   *
   * @param longitude < 0 si Est (en degrées décimales)
   * @return heures décimales d'un angle (modulo 24)
   * @throws Exception
   */
  public static double getLocaleMeanSideralTime(double longitude) throws Exception {
    Calendar calUTC = getCurrentCalendarUTC();
    int hTEST = calUTC.get(Calendar.HOUR_OF_DAY);
    double JJ_atMidnight = getJulianDay(calUTC,true);
    double jcu_stForMidnightJD = sideralTimeInJulianCenturyUnity(JJ_atMidnight);
    double result= (getMeanGreenwichSideralTimeInDegrees(
            jcu_stForMidnightJD,getSecondOfDay(calUTC))- longitude/15.0) % 24.0;
    if(result<0.0)
      result = 24.0 + result;
    return result;
  }

  public static int getSecondOfDay(Calendar cal) {
    return cal.get(Calendar.HOUR_OF_DAY)*3600 +
            cal.get(Calendar.MINUTE)*60 + cal.get(Calendar.SECOND);

  }


  //NOT USED----------------------------------------------------------------------------------
  /**
   * Aussi précis que getJulianDay mais algoritme plus lourd et non utile car le programme
   * utilise toujours des dates grégoriennes !
   * http://fr.wikipedia.org/wiki/Jour_julien
   * Algorithme de conversion d'une date du calendrier julien ou grégorien en jours juliens
   * @param calUTC
   * @param G (gregorian=1, sinon =0)
   * @return
   * @throws Exception
   * aà 12h JJ -0.5 pour 0h JJ
   */
  public static double getGeneralJulianDay(Calendar calUTC,int G) throws Exception {
    int A = calUTC.get(Calendar.YEAR);
    int M = calUTC.get(Calendar.MONTH)+1;
    double S = 1.0;
    int B = Math.abs(M-9);
    if(M<9)
      S = -1.0;
    double J1 = Math_lib.integerPart(A+S*Math_lib.integerPart(B/7.0));
    double J2 = -Math_lib.integerPart((Math_lib.integerPart(J1/100.0)+1.0)*0.75);
    double Q = getDecimalDay(calUTC);

    double JJ = -Math_lib.integerPart(7.0*(Math_lib.integerPart((M+9.0)/12.0)+A)/4.0)+
            Math_lib.integerPart(275.0*M/9.0) + Q + G*(J2 + 2.0) + 367.0*A + 1721027.0 +0.5;
    String test ="ok";
    return JJ;
  }

  /**
   * Pas utile car ok mais valeur arrondie !!!
   * http://developer.android.com/reference/android/text/format/Time.html#gmtoff
   * http://developer.android.com/reference/android/text/format/Time.html#getJulianDay(long, long)
   * public long gmtoff Offset in seconds from UTC including any DST offset.
   *
   * @return
   */
  public static int currentJulianDay() {
    Time time = new Time();
    time.timezone = "UTC";
    time.setToNow();
    long millis =  time.toMillis(false);
    int jd = Time.getJulianDay(millis, 0L);
    return jd;
  }
  //---------------------------------------------------------------------------------------











  //NOT USED----------------------------------------------------------------------------------



  /**
   * This method always returns UTC times, regardless of the system's time zone.
   * This is often called "Unix time" or "epoch time"
   * Use a DateFormat instance to format this time for display to a human.
   *
   * @return current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
   */
  /*public static long utc_millisecondsSince1970() {
    return System.currentTimeMillis();
  }*/

  /**
   * Etc/UTC | Etc/Greenwich | Etc/Universal | GMT
   * http://developer.android.com/reference/android/text/format/Time.html
   * http://developer.android.com/reference/java/util/TimeZone.html
   * <p/>
   * The time is initialized to Jan 1, 1970.
   *
   * @return
   */
  /*public static long utcMillisecondsSinceEpoch() {
    Time time = new Time();
    time.timezone = "UTC";
    return time.normalize(true);
  }*/


/*
  public static String getStringFormatDate_forDate(Date date, String formatPattern) throws Exception {
    SimpleDateFormat df = new SimpleDateFormat(formatPattern);
    return df.format(date);
  }

  public static Date getDefaultDate_forStringFormatDate(String dateString) throws Exception {
    DateFormat defaultDateFormat = DateFormat.getDateInstance();
    Date defaultDate = defaultDateFormat.parse(dateString);
    return defaultDate;
  }

  public static Calendar getCalendarInstance_forDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }
*/
//--------------------------------- ----------- ------------------

/*
  public static void compareCalendar(Calendar c1, Calendar c2, Calendar c3, Calendar c4) {
    String dateFormat = "  yyyy-MM-dd'T'HH:mm:ss.SS";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String cal1_isoString = sdf.format(c1.getTime());
    String cal2_isoString = sdf.format(c2.getTime());
    String cal3_isoString = sdf.format(c3.getTime());
    String cal4_isoString = sdf.format(c4.getTime());
    String end = "end";
  }*/


  /**
   * NOT USED BECAUSE ALWAYS THE SAME HASHCODE !
   * also with new GregorianCalendar(TimeZone.getTimeZone("UTC"));
   * USED Calendar copy = besselDate if you want only a copy with the same hashcode !
   * @param
   * @return
   * @throws ParseException
   *//*
    public static Calendar getUTC_calendarCopy(Calendar besselDate) throws ParseException {
        int besselDateHC = besselDate.hashCode();
        Calendar copy = getCurrentCalendarUTC();// always the same hashcode !
        int copyHC = copy.hashCode();
        copy.set(Calendar.YEAR, besselDate.get(Calendar.YEAR));
        copy.set(Calendar.MONTH,besselDate.get(Calendar.MONTH));
        copy.set(Calendar.DAY_OF_YEAR,besselDate.get(Calendar.DAY_OF_YEAR));
        setHHmmssSSS_calendar(copy,besselDate.get(Calendar.HOUR_OF_DAY),
                besselDate.get(Calendar.MINUTE),besselDate.get(Calendar.SECOND),
                besselDate.get(Calendar.MILLISECOND));
        int copyHC2 = copy.hashCode();

        Calendar c1, c2;
        c1 = Calendar.getInstance();
        int c1HC1 = c1.hashCode();
        c2 = Calendar.getInstance();
        int c2HC = c1.hashCode();
        c1.add(Calendar.MONTH, 1);
        int c1HC2 = c1.hashCode();

        return copy;
    }   */


}