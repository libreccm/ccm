/* -*- mode: java; c-basic-offset: 4; indent-tabs-mode: nil -*-
 *
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.auth.http;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * <p>
 * This class represents an IPV4 address range, such as 192.168.1.0/24
 * or 192.168.1.0/255.255.255.0 (both these examples are equivalent).
 * An address range consists of an address and a netmask.
 * </p>
 *
 * <p>
 * At the time that I wrote this class, I wasn't aware of any standard
 * class which supported this feature. However, it is clear that at some
 * point in the future such a class will be added to Java, at which
 * point this code will be redundant.
 * </p>
 *
 * <p>
 * NB. This will NOT work with JDK 1.4. To make it work, you need to
 * manually replace all references to <code>InetAddress</code> to
 * <code>Inet4Address</code>. Jeez, how much do I hate Java.
 * </p>
 *
 * @author Richard W.M. Jones
 */
public class Inet4AddressRange
{
  private InetAddress m_address;
  private InetAddress m_netmask;

  /**
   * <p>
   * Given a string in one of the valid forms below, construct an
   * IPV4 address range.
   * </p>
   *
   * <p>
   * The valid forms for the string are:
   * </p>
   *
   * <ul>
   * <li> <code>"192.168.1.0/24"</code> </li>
   * <li> <code>"192.168.1.0/255.255.255.0"</code> </li>
   * <li> <code>"192.168.1.15"</code> (the implicit netmask is
   *      255.255.255.255). </li>
   * </ul>
   *
   * <p>
   * If the string has an invalid format, this returns <code>null</code>.
   * </p>
   *
   * @param s The string.
   * @return <code>Inet4AddressRange</code> object or <code>null</code>.
   */
  public static Inet4AddressRange getByName (String s)
  {
    InetAddress address, netmask;

    try {
      int i = s.indexOf ('/');

      // If no "/" in the string, set netmask to 255.255.255.255 and try
      // to set the address.
      if (i == -1)
        {
          netmask = makeNetmask (32);
          address = InetAddress.getByName (s);
        }
      else
        {
          // Split the string on the first "/" character.
          String addrString = s.substring (0, i);
          String netmaskString = s.substring (i+1, s.length ());

          address = InetAddress.getByName (addrString);

          // If the netmask is NOT just a simple number, resolve it.
          if (!isNumber (netmaskString))
            netmask = InetAddress.getByName (netmaskString);
          // Otherwise the netmask is a simple number so infer it.
          else
            netmask = makeNetmask (Integer.parseInt (netmaskString));
        }
    }
    catch (UnknownHostException ex) {
      return null;
    }
    catch (SecurityException ex) {
      throw new UncheckedWrapperException (ex);
    }

    return new Inet4AddressRange (address, netmask);
  }

  /**
   * Returns true iff the string parameter looks like a number.
   */
  private static boolean isNumber (String s)
  {
    for (int i = 0; i < s.length(); ++i)
      if (s.charAt (i) < '0' || s.charAt (i) > '9')
        return false;
    return true;
  }

  /**
   * Given an address and netmask, construct an object of this class.
   */
  public Inet4AddressRange (InetAddress address, InetAddress netmask)
  {
    m_address = address;
    m_netmask = netmask;
  }

  /**
   * Return the address field of this object.
   */
  public InetAddress getAddress ()
  {
    return m_address;
  }

  /**
   * Return the netmask field of this object.
   */
  public InetAddress getNetmask ()
  {
    return m_netmask;
  }

  /**
   * Convert to a printable string.
   */
  public String toString ()
  {
    return
      inetAddressToString (m_address) + "/" + inetAddressToString (m_netmask);
  }

  /**
   * Return true if and only if two Inet4AddressRange objects are
   * semantically equal.
   */
  public boolean equals (Inet4AddressRange other)
  {
    return
      m_address.equals (other.m_address) &&
      m_netmask.equals (other.m_netmask);
  }

  /**
   * Return true if the address parameter given is inside this range
   * of addresses.
   */
  public boolean inRange (InetAddress address)
  {
    long maddressLong = inetAddressToLong (m_address);
    long mnetmaskLong = inetAddressToLong (m_netmask);
    long addressLong = inetAddressToLong (address);

    return (addressLong & mnetmaskLong) == maddressLong;
  }

  /**
   * This is the function we use to convert an <code>InetAddress</code>
   * to a long (to get around the stupid lack of unsigned types in Java).
   * This is public because it might be generally useful.
   */
  public static long inetAddressToLong (InetAddress address)
  {
    byte[] bytes = address.getAddress ();
    long a = bytes[0], b = bytes[1], c = bytes[2], d = bytes[3];
    a &= 0xff; b &= 0xff; c &= 0xff; d &= 0xff;
    return (a << 24) | (b << 16) | (c << 8) | d;
  }

  /**
   * This is the function we use to convert a long to an
   * <code>InetAddress</code>.  This is public because it might be
   * generally useful.
   */
  public static InetAddress longToInetAddress (long address)
  {
    try {
      return InetAddress.getByName (String.valueOf (address));
    }
    catch (UnknownHostException ex) {
      throw new UncheckedWrapperException (ex);
    }
    catch (SecurityException ex) {
      throw new UncheckedWrapperException (ex);
    }
  }

  /**
   * Convert an <code>InetAddress</code> to a string. The normal
   * <code>toString</code> method is pretty useless.
   */
  public static String inetAddressToString (InetAddress address)
  {
    byte[] bytes = address.getAddress ();
    long a = bytes[0], b = bytes[1], c = bytes[2], d = bytes[3];
    a &= 0xff; b &= 0xff; c &= 0xff; d &= 0xff;
    return a + "." + b + "." + c + "." + d;
  }

  /**
   * Private function to construct a netmask from a number.
   * eg. 24 will return the address 255.255.255.0.
   */
  private static InetAddress makeNetmask (int bits)
    throws UnknownHostException, SecurityException
  {
    long n = (0xffffffff00000000L >> bits) & 0xffffffffL;
    return longToInetAddress (n);
  }

  /**
   * Test suite for this class.
   */
  public static void main (String[] args)
    throws Exception
  {
    // Test inetAddressToLong and longToInetAddress.
    InetAddress addr = InetAddress.getByName ("192.168.0.99");
    InetAddress addr2 = longToInetAddress (inetAddressToLong (addr));
    String addrStr = inetAddressToString (addr);
    String addr2Str = inetAddressToString (addr2);
    System.out.println ("addr = " + addrStr + "; addr2 = " + addr2Str);
    Assert.truth (addrStr.equals (addr2Str));

    // Test makeNetmask.
    addr = makeNetmask (0);
    System.out.println ("makeNetmask(0) = " + addr);
    Assert.truth (inetAddressToString (addr).equals ("0.0.0.0"));

    addr = makeNetmask (8);
    System.out.println ("makeNetmask(8) = " + addr);
    Assert.truth (inetAddressToString (addr).equals ("255.0.0.0"));

    addr = makeNetmask (16);
    System.out.println ("makeNetmask(16) = " + addr);
    Assert.truth (inetAddressToString (addr).equals ("255.255.0.0"));

    addr = makeNetmask (24);
    System.out.println ("makeNetmask(24) = " + addr);
    Assert.truth (inetAddressToString (addr).equals ("255.255.255.0"));

    addr = makeNetmask (28);
    System.out.println ("makeNetmask(28) = " + addr);
    Assert.truth (inetAddressToString (addr).equals ("255.255.255.240"));

    addr = makeNetmask (32);
    System.out.println ("makeNetmask(32) = " + addr);
    Assert.truth (inetAddressToString (addr).equals ("255.255.255.255"));

    // Test getByName.
    Inet4AddressRange range
      = Inet4AddressRange.getByName ("192.168.0.0/16");
    System.out.println ("range = " + range.toString ());

    Inet4AddressRange range2
      = Inet4AddressRange.getByName ("192.168.0.0/255.255.0.0");
    System.out.println ("range = " + range.toString ());
    // Test equals.
    Assert.truth (range.equals (range2));

    range = Inet4AddressRange.getByName ("192.168.0.99");
    System.out.println ("range = " + range.toString ());

    // Test inRange.
    range = Inet4AddressRange.getByName ("192.168.0.0/16");
    addr = InetAddress.getByName ("192.168.0.99");
    Assert.truth (range.inRange (addr));

    range = Inet4AddressRange.getByName ("192.168.0.0/24");
    addr = InetAddress.getByName ("192.168.2.99");
    Assert.truth (! range.inRange (addr));

    range = Inet4AddressRange.getByName ("192.168.0.99");
    addr = InetAddress.getByName ("192.168.0.99");
    Assert.truth (range.inRange (addr));

    range = Inet4AddressRange.getByName ("192.168.0.99");
    addr = InetAddress.getByName ("192.168.3.99");
    Assert.truth (! range.inRange (addr));

    System.out.println ("All test completed OK.");
  }
};
