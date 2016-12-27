/*
Copyright (C) 2013, 2014 Olivier Fauchon
This file is part of SMS-TWEAK Android Aplication.

SMS-TWEAK is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SMS-TWEAK is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
*/

package com.oflabs.smstweak;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 7/28/11
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */




public class RulesParser {

    private String callerid;
    private String messagebody;

    private final String TOK_DAYOFMONTH = "dayofmonth";
    private final String TOK_MONTHOFYEAR = "monthofyear";

    private final String[] tLogic = {"and" , "or"};
    private final String[] tSpecial = {TOK_DAYOFMONTH , "hourofday" , "callerid" , "message"};


    public void setMessagebody(String messagebody) {
        this.messagebody = messagebody;
    }
    public void setCallerid(String callerid) {
        this.callerid = callerid;
    }
    private void doLog(String pLog){
        System.out.println("D:"+pLog);
    }
    private boolean isSpecial(String s)
    {
        return Arrays.asList(tSpecial).contains(s);
    }
    private boolean isLogic(String s)
    {
        return Arrays.asList(tLogic).contains(s);
    }

    /*
     * Rule cleanup : lowercase, normalize spaces..
     */
    private String cleanRule(String rule)
    {
      //  System.out.println("cleanRule: PRE: " + rule+"\n");
        rule = rule.replaceAll("([\\(\\)><])" ," $1 ") ;
      //  System.out.println("cleanRule: POST: " + rule + "\n");
       return rule;
    }

    private int pGetInt(String s) throws ParserException
    {
        int ret=0;
        if (s.equals("hourofday")){
            ret = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        } else if (s.equals("dayofmonth")){
            ret = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        } else {
            try{
                        ret = Integer.parseInt(s);
            }catch (NumberFormatException e)
                {
                    throw new ParserException("'" + s + "'  is not an integer, or special token unknown.");
                }

        }
        return ret;

    }

    private boolean computeSpecial(String t1, String t2, String t3) throws ParserException
    {
        boolean ret= false;
        String op1;

        // Remove brackets.
        if (t3.startsWith("\"") && t3.endsWith("\""))
            t3=t3.substring(1,t3.length()-1);

        // >,<,=
        if (t2.equals(">") || t2.equals("<") || t2.equals("="))
        {
            int para1,para2;
            para1 = pGetInt(t1);
            para2 = pGetInt(t3);
            if (t2.equals(">")) ret = (para1 > para2);
            else if (t2.equals("<")) ret = (para1 < para2);
            else if (t2.equals("=")) ret = (para1 == para2);
            return ret;
        }

        if (t1.equals("callerid")) t1 = callerid;
        else if (t1.equals("message")) t1 = messagebody;
        else throw new ParserException("Unknown keyword" + t1 );

        if (t2.endsWith("equals"))
            ret=t1.equals(t3);
        else if (t2.endsWith("icontains")) // Must be before contains.
            ret=t1.toLowerCase().contains(t3.toLowerCase());
        else if (t2.endsWith("contains"))
            ret=t1.contains(t3);
        else throw new ParserException("Unknown Operator (" + t2+")");

        if (t2.startsWith("!")) ret=!ret;

        return ret;

    }

        private boolean computeLogic(String op, boolean b1, boolean b2) throws ParserException
    {
        boolean ret= false;

        if (op.equals("and")) ret = b1 && b2;
        else if (op.equals("or")) ret = b1 || b2;
        else throw new ParserException("Unknown Logic Op");
        return ret;

    }

    private List<String> cleanTokens(List<String> pTokList){
        // Merge tokens composing a splited string
        String curTok="";
        for (int i = 0; i< pTokList.size(); i++){
            curTok=pTokList.get(i);
            // Multipart String
            if (curTok.startsWith("\"") && !curTok.endsWith("\"")){
                boolean fin = false;
                for (int j = i+1; j<pTokList.size() && !fin; i++){
                    String ntok = pTokList.get(j);
                    curTok = curTok + " " + ntok;
                    pTokList.remove(j);
                    if (curTok.endsWith("\"")){
                        pTokList.set(i,curTok);
                        fin=true;
                    }

                }

            }
        }
        //Tolowercase
        for (int i = 0; i< pTokList.size(); i++){
            curTok=pTokList.get(i);
            if (!curTok.startsWith("\"")) pTokList.set(i,curTok.toLowerCase());
        }


        return pTokList;
    }
    /*
    Evaluate a rule
     */
  public boolean evaluate(String expstr) throws ParserException
  {
      expstr = cleanRule(expstr);

      List<String> tokList = new ArrayList<String>();

      StringTokenizer st = new StringTokenizer(expstr," ",false);
      while (st.hasMoreTokens()){
          tokList.add(st.nextToken());
      }
      tokList = cleanTokens(tokList);

     return evaluate(true, tokList);

  }

    /*
    Evaluate a rule
     */
  public boolean evaluate(boolean state, List<String> tokList) throws ParserException
  {

      boolean ret=state;

      String t1,t2, t3;
      for (int i=0; i<tokList.size(); i++){
          t1 = tokList.get(i);
          // Process special comparisons (number, body....)
          if (isSpecial(t1)){
            if ((i+2) < tokList.size())
            {
              t2 = tokList.get(i+1);
              t3 = tokList.get(i+2);
              ret = computeSpecial(t1,t2, t3);
              i+=2;
            } else throw new ParserException("Special ("+ t1 +") must be followed by operator and value");
          }
          // Process Logic operators (and,or)
          else if (isLogic(t1))
          {
              boolean ret2 = evaluate(false, tokList.subList(i+1,tokList.size()));
              ret = computeLogic(t1,ret,ret2);
              break;
          }
          else if (t1.equals("(") )
           {
               int z = tokList.indexOf(")");
               if (z== -1) throw new ParserException("Can't find matching ')' ");
               ret = evaluate(false, tokList.subList(i+1,z));
               break;
           }else
          {
              throw new ParserException("Token "+Integer.toString(i)+" ("+ t1 +") is neither Special nor Logic");
          }



      }

    return ret;
  }




public static void main(String[] args){

    class RuleTest{
        private String testName;
        private String testString;
        private boolean result;

        private RuleTest(String testName, String testString, boolean result) {
            this.testName = testName;
            this.testString = testString;
            this.result = result;
        }
    }


    RulesParser p = new RulesParser();
    p.setCallerid("+33610493763");
    p.setMessagebody("DOWN server, this is a problem");

    ArrayList<RuleTest> r = new ArrayList<RuleTest>();

    r.add(new RuleTest(
            "1:Simple callerid and message match", "callerid equals \"+33610493763\" and message contains \"DOWN\" ", true));
    r.add(new RuleTest(
            "2:Case Mix match, with braces",
            "callerID ContainS \"610493763\" and ( meSSage cONtains \"DOWN\" aND messAge contains \"is a\" )",
            true));
    r.add(new RuleTest(
            "3: or and negate",
            "( callerid !ContainS \"3655\" ) or ( message !cONtains \"DOWN\" )",
            true));

    r.add(new RuleTest(
            "4: 0 or 2+ spaces between tokens",
            "( callerid !ContainS \"3655\" ) or ( message !cONtains \"DOWN\" )",
            true));

    r.add(new RuleTest(
            "4: sup, inf, equal",
            "dayofmonth < 30",
            true));

    r.add(new RuleTest(
            "5: sup, inf, equal",
            "dayofmonth > 0",
            true));

    r.add(new RuleTest(
            "6: space pre-post braces",
            "(dayofmonth > 0) and (hourofday > 0)",
            true));


    r.add(new RuleTest(
            "7: real case",
            "number",
            true));




    Iterator it = r.iterator();
    RuleTest rt;
    boolean result =  false;
    while (it.hasNext()){
        rt = (RuleTest)it.next();
        System.out.println("* " + rt.testName + " \n> " + rt.testString+ "  ");
        try {
            result=p.evaluate(rt.testString);
        } catch (ParserException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (result == rt.result)
            System.out.println("PASS\n");
        else
            System.out.println("FAILED");


    }


}              //

}
