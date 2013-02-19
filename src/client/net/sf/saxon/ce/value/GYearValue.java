package client.net.sf.saxon.ce.value;

import client.net.sf.saxon.ce.om.StandardNames;
import client.net.sf.saxon.ce.trans.XPathException;
import client.net.sf.saxon.ce.tree.util.FastStringBuffer;
import client.net.sf.saxon.ce.type.AtomicType;
import client.net.sf.saxon.ce.type.BuiltInAtomicType;
import client.net.sf.saxon.ce.type.ConversionResult;
import client.net.sf.saxon.ce.type.ValidationFailure;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;


/**
 * Implementation of the xs:gYear data type
 */

public class GYearValue extends GDateValue {

    private static RegExp regex =
            RegExp.compile("(-?[0-9]+)(Z|[+-][0-9][0-9]:[0-9][0-9])?");

    private GYearValue(){}

    public static ConversionResult makeGYearValue(CharSequence value) {
        GYearValue g = new GYearValue();
        MatchResult m = regex.exec(Whitespace.trimWhitespace(value).toString());
        if (m == null) {
            return new ValidationFailure("Cannot convert '" + value + "' to a gYear");
        }
        String base = m.getGroup(1);
        String tz = m.getGroup(2);
        String date = (base==null ? "" : base) + "-01-01" + (tz==null ? "" : tz);
        g.typeLabel = BuiltInAtomicType.G_YEAR;
        return setLexicalValue(g, date);
    }

    public GYearValue(int year, int tz) {
        this(year, tz, BuiltInAtomicType.G_YEAR);
    }

    public GYearValue(int year, int tz, AtomicType type) {
        this.year = year;
        this.month = 1;
        this.day = 1;
        setTimezoneInMinutes(tz);
        this.typeLabel = type;
    }

    /**
     * Make a copy of this date, time, or dateTime value
     */

    public AtomicValue copy() {
        GYearValue v = new GYearValue(year, getTimezoneInMinutes());
        v.typeLabel = typeLabel;
        return v;
    }

    /**
     * Determine the primitive type of the value. This delivers the same answer as
     * getItemType().getPrimitiveItemType(). The primitive types are
     * the 19 primitive types of XML Schema, plus xs:integer, xs:dayTimeDuration and xs:yearMonthDuration,
     * and xs:untypedAtomic. For external objects, the result is AnyAtomicType.
     */

    public BuiltInAtomicType getPrimitiveType() {
        return BuiltInAtomicType.G_YEAR;
    }

    /**
    * Convert to target data type
    * @param requiredType an integer identifying the required atomic type
    * @return an AtomicValue, a value of the required type; or an ErrorValue
    */

    public ConversionResult convertPrimitive(BuiltInAtomicType requiredType, boolean validate)  {
        switch(requiredType.getPrimitiveType()) {
        case StandardNames.XS_G_YEAR:
        case StandardNames.XS_ANY_ATOMIC_TYPE:
            return this;

        case StandardNames.XS_STRING:
            return new StringValue(getStringValueCS());
        case StandardNames.XS_UNTYPED_ATOMIC:
            return new UntypedAtomicValue(getStringValueCS());
        default:
            ValidationFailure err = new ValidationFailure("Cannot convert gYear to " +
                                     requiredType.getDisplayName());
            err.setErrorCode("XPTY0004");
            return err;
        }
    }

    public CharSequence getPrimitiveStringValue() {

        FastStringBuffer sb = new FastStringBuffer(FastStringBuffer.TINY);
        int yr = year;
        if (year <= 0) {
            yr = -yr + 1;           // no year zero in lexical space for XSD 1.0
            if(yr!=0){
                sb.append('-');    
            }
        }
        appendString(sb, yr, (yr>9999 ? (yr+"").length() : 4));

        if (hasTimezone()) {
            appendTimezone(sb);
        }

        return sb;

    }

    /**
     * Add a duration to this date/time value
     *
     * @param duration the duration to be added (which might be negative)
     * @return a new date/time value representing the result of adding the duration. The original
     *         object is not modified.
     * @throws client.net.sf.saxon.ce.trans.XPathException
     *
     */

    public CalendarValue add(DurationValue duration) throws XPathException {
        XPathException err = new XPathException("Cannot add a duration to an xs:gYear");
        err.setErrorCode("XPTY0004");
        throw err;
    }

    /**
     * Return a new date, time, or dateTime with the same normalized value, but
     * in a different timezone
     *
     * @param tz the new timezone, in minutes
     * @return the date/time in the new timezone
     */

    public CalendarValue adjustTimezone(int tz) {
        DateTimeValue dt = (DateTimeValue)toDateTime().adjustTimezone(tz);
        return new GYearValue(dt.getYear(), dt.getTimezoneInMinutes());
    }
}

// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
// If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
// This Source Code Form is “Incompatible With Secondary Licenses”, as defined by the Mozilla Public License, v. 2.0.