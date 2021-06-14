//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.spi.ExceptionHandler;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.spi.PropertyConfigurerGetter;
import org.apache.camel.support.component.PropertyConfigurerSupport;

public class JiraEndpointConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {
    public JiraEndpointConfigurer() {
    }

    public boolean configure(CamelContext camelContext, Object obj, String name, Object value, boolean ignoreCase) {
        JiraEndpoint target = (JiraEndpoint)obj;
        String var7 = ignoreCase ? name.toLowerCase() : name;
        byte var8 = -1;
        switch(var7.hashCode()) {
            case -2033401843:
                if (var7.equals("exchangepattern")) {
                    var8 = 9;
                }
                break;
            case -2023483918:
                if (var7.equals("maxResults")) {
                    var8 = 17;
                }
                break;
            case -1876070948:
                if (var7.equals("privateKey")) {
                    var8 = 20;
                }
                break;
            case -1876040196:
                if (var7.equals("privatekey")) {
                    var8 = 19;
                }
                break;
            case -1730917813:
                if (var7.equals("bridgeerrorhandler")) {
                    var8 = 2;
                }
                break;
            case -1594540319:
                if (var7.equals("jiraUrl")) {
                    var8 = 12;
                }
                break;
            case -1594509567:
                if (var7.equals("jiraurl")) {
                    var8 = 11;
                }
                break;
            case -1391566821:
                if (var7.equals("exceptionhandler")) {
                    var8 = 7;
                }
                break;
            case -1042689291:
                if (var7.equals("accessToken")) {
                    var8 = 1;
                }
                break;
            case -1013136619:
                if (var7.equals("accesstoken")) {
                    var8 = 0;
                }
                break;
            case -858417527:
                if (var7.equals("consumerKey")) {
                    var8 = 5;
                }
                break;
            case -858386775:
                if (var7.equals("consumerkey")) {
                    var8 = 4;
                }
                break;
            case -600261888:
                if (var7.equals("lazystartproducer")) {
                    var8 = 14;
                }
                break;
            case -368748563:
                if (var7.equals("exchangePattern")) {
                    var8 = 10;
                }
                break;
            case -265713450:
                if (var7.equals("username")) {
                    var8 = 23;
                }
                break;
            case 105477:
                if (var7.equals("jql")) {
                    var8 = 13;
                }
                break;
            case 95467907:
                if (var7.equals("delay")) {
                    var8 = 6;
                }
                break;
            case 98055755:
                if (var7.equals("bridgeErrorHandler")) {
                    var8 = 3;
                }
                break;
            case 263343872:
                if (var7.equals("lazyStartProducer")) {
                    var8 = 15;
                }
                break;
            case 273086459:
                if (var7.equals("exceptionHandler")) {
                    var8 = 8;
                }
                break;
            case 406637063:
                if (var7.equals("watchedFields")) {
                    var8 = 27;
                }
                break;
            case 606830098:
                if (var7.equals("maxresults")) {
                    var8 = 16;
                }
                break;
            case 642216616:
                if (var7.equals("verificationCode")) {
                    var8 = 25;
                }
                break;
            case 643169928:
                if (var7.equals("verificationcode")) {
                    var8 = 24;
                }
                break;
            case 1216985755:
                if (var7.equals("password")) {
                    var8 = 18;
                }
                break;
            case 1322769895:
                if (var7.equals("watchedfields")) {
                    var8 = 26;
                }
                break;
            case 1425433203:
                if (var7.equals("sendOnlyUpdatedField")) {
                    var8 = 22;
                }
                break;
            case 2133441107:
                if (var7.equals("sendonlyupdatedfield")) {
                    var8 = 21;
                }
        }

        switch(var8) {
            case 0:
            case 1:
                target.getConfiguration().setAccessToken((String)property(camelContext, String.class, value));
                return true;
            case 2:
            case 3:
                target.setBridgeErrorHandler((Boolean)property(camelContext, Boolean.TYPE, value));
                return true;
            case 4:
            case 5:
                target.getConfiguration().setConsumerKey((String)property(camelContext, String.class, value));
                return true;
            case 6:
                target.getConfiguration().setDelay((Integer)property(camelContext, Integer.class, value));
                return true;
            case 7:
            case 8:
                target.setExceptionHandler((ExceptionHandler)property(camelContext, ExceptionHandler.class, value));
                return true;
            case 9:
            case 10:
                target.setExchangePattern((ExchangePattern)property(camelContext, ExchangePattern.class, value));
                return true;
            case 11:
            case 12:
                target.getConfiguration().setJiraUrl((String)property(camelContext, String.class, value));
                return true;
            case 13:
                target.setJql((String)property(camelContext, String.class, value));
                return true;
            case 14:
            case 15:
                target.setLazyStartProducer((Boolean)property(camelContext, Boolean.TYPE, value));
                return true;
            case 16:
            case 17:
                target.setMaxResults((Integer)property(camelContext, Integer.class, value));
                return true;
            case 18:
                target.getConfiguration().setPassword((String)property(camelContext, String.class, value));
                return true;
            case 19:
            case 20:
                target.getConfiguration().setPrivateKey((String)property(camelContext, String.class, value));
                return true;
            case 21:
            case 22:
                target.setSendOnlyUpdatedField((Boolean)property(camelContext, Boolean.TYPE, value));
                return true;
            case 23:
                target.getConfiguration().setUsername((String)property(camelContext, String.class, value));
                return true;
            case 24:
            case 25:
                target.getConfiguration().setVerificationCode((String)property(camelContext, String.class, value));
                return true;
            case 26:
            case 27:
                target.setWatchedFields((String)property(camelContext, String.class, value));
                return true;
            default:
                return false;
        }
    }

    public Class<?> getOptionType(String name, boolean ignoreCase) {
        String var3 = ignoreCase ? name.toLowerCase() : name;
        byte var4 = -1;
        switch(var3.hashCode()) {
            case -2033401843:
                if (var3.equals("exchangepattern")) {
                    var4 = 9;
                }
                break;
            case -2023483918:
                if (var3.equals("maxResults")) {
                    var4 = 17;
                }
                break;
            case -1876070948:
                if (var3.equals("privateKey")) {
                    var4 = 20;
                }
                break;
            case -1876040196:
                if (var3.equals("privatekey")) {
                    var4 = 19;
                }
                break;
            case -1730917813:
                if (var3.equals("bridgeerrorhandler")) {
                    var4 = 2;
                }
                break;
            case -1594540319:
                if (var3.equals("jiraUrl")) {
                    var4 = 12;
                }
                break;
            case -1594509567:
                if (var3.equals("jiraurl")) {
                    var4 = 11;
                }
                break;
            case -1391566821:
                if (var3.equals("exceptionhandler")) {
                    var4 = 7;
                }
                break;
            case -1042689291:
                if (var3.equals("accessToken")) {
                    var4 = 1;
                }
                break;
            case -1013136619:
                if (var3.equals("accesstoken")) {
                    var4 = 0;
                }
                break;
            case -858417527:
                if (var3.equals("consumerKey")) {
                    var4 = 5;
                }
                break;
            case -858386775:
                if (var3.equals("consumerkey")) {
                    var4 = 4;
                }
                break;
            case -600261888:
                if (var3.equals("lazystartproducer")) {
                    var4 = 14;
                }
                break;
            case -368748563:
                if (var3.equals("exchangePattern")) {
                    var4 = 10;
                }
                break;
            case -265713450:
                if (var3.equals("username")) {
                    var4 = 23;
                }
                break;
            case 105477:
                if (var3.equals("jql")) {
                    var4 = 13;
                }
                break;
            case 95467907:
                if (var3.equals("delay")) {
                    var4 = 6;
                }
                break;
            case 98055755:
                if (var3.equals("bridgeErrorHandler")) {
                    var4 = 3;
                }
                break;
            case 263343872:
                if (var3.equals("lazyStartProducer")) {
                    var4 = 15;
                }
                break;
            case 273086459:
                if (var3.equals("exceptionHandler")) {
                    var4 = 8;
                }
                break;
            case 406637063:
                if (var3.equals("watchedFields")) {
                    var4 = 27;
                }
                break;
            case 606830098:
                if (var3.equals("maxresults")) {
                    var4 = 16;
                }
                break;
            case 642216616:
                if (var3.equals("verificationCode")) {
                    var4 = 25;
                }
                break;
            case 643169928:
                if (var3.equals("verificationcode")) {
                    var4 = 24;
                }
                break;
            case 1216985755:
                if (var3.equals("password")) {
                    var4 = 18;
                }
                break;
            case 1322769895:
                if (var3.equals("watchedfields")) {
                    var4 = 26;
                }
                break;
            case 1425433203:
                if (var3.equals("sendOnlyUpdatedField")) {
                    var4 = 22;
                }
                break;
            case 2133441107:
                if (var3.equals("sendonlyupdatedfield")) {
                    var4 = 21;
                }
        }

        switch(var4) {
            case 0:
            case 1:
                return String.class;
            case 2:
            case 3:
                return Boolean.TYPE;
            case 4:
            case 5:
                return String.class;
            case 6:
                return Integer.class;
            case 7:
            case 8:
                return ExceptionHandler.class;
            case 9:
            case 10:
                return ExchangePattern.class;
            case 11:
            case 12:
                return String.class;
            case 13:
                return String.class;
            case 14:
            case 15:
                return Boolean.TYPE;
            case 16:
            case 17:
                return Integer.class;
            case 18:
                return String.class;
            case 19:
            case 20:
                return String.class;
            case 21:
            case 22:
                return Boolean.TYPE;
            case 23:
                return String.class;
            case 24:
            case 25:
                return String.class;
            case 26:
            case 27:
                return String.class;
            default:
                return null;
        }
    }

    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        JiraEndpoint target = (JiraEndpoint)obj;
        String var5 = ignoreCase ? name.toLowerCase() : name;
        byte var6 = -1;
        switch(var5.hashCode()) {
            case -2033401843:
                if (var5.equals("exchangepattern")) {
                    var6 = 9;
                }
                break;
            case -2023483918:
                if (var5.equals("maxResults")) {
                    var6 = 17;
                }
                break;
            case -1876070948:
                if (var5.equals("privateKey")) {
                    var6 = 20;
                }
                break;
            case -1876040196:
                if (var5.equals("privatekey")) {
                    var6 = 19;
                }
                break;
            case -1730917813:
                if (var5.equals("bridgeerrorhandler")) {
                    var6 = 2;
                }
                break;
            case -1594540319:
                if (var5.equals("jiraUrl")) {
                    var6 = 12;
                }
                break;
            case -1594509567:
                if (var5.equals("jiraurl")) {
                    var6 = 11;
                }
                break;
            case -1391566821:
                if (var5.equals("exceptionhandler")) {
                    var6 = 7;
                }
                break;
            case -1042689291:
                if (var5.equals("accessToken")) {
                    var6 = 1;
                }
                break;
            case -1013136619:
                if (var5.equals("accesstoken")) {
                    var6 = 0;
                }
                break;
            case -858417527:
                if (var5.equals("consumerKey")) {
                    var6 = 5;
                }
                break;
            case -858386775:
                if (var5.equals("consumerkey")) {
                    var6 = 4;
                }
                break;
            case -600261888:
                if (var5.equals("lazystartproducer")) {
                    var6 = 14;
                }
                break;
            case -368748563:
                if (var5.equals("exchangePattern")) {
                    var6 = 10;
                }
                break;
            case -265713450:
                if (var5.equals("username")) {
                    var6 = 23;
                }
                break;
            case 105477:
                if (var5.equals("jql")) {
                    var6 = 13;
                }
                break;
            case 95467907:
                if (var5.equals("delay")) {
                    var6 = 6;
                }
                break;
            case 98055755:
                if (var5.equals("bridgeErrorHandler")) {
                    var6 = 3;
                }
                break;
            case 263343872:
                if (var5.equals("lazyStartProducer")) {
                    var6 = 15;
                }
                break;
            case 273086459:
                if (var5.equals("exceptionHandler")) {
                    var6 = 8;
                }
                break;
            case 406637063:
                if (var5.equals("watchedFields")) {
                    var6 = 27;
                }
                break;
            case 606830098:
                if (var5.equals("maxresults")) {
                    var6 = 16;
                }
                break;
            case 642216616:
                if (var5.equals("verificationCode")) {
                    var6 = 25;
                }
                break;
            case 643169928:
                if (var5.equals("verificationcode")) {
                    var6 = 24;
                }
                break;
            case 1216985755:
                if (var5.equals("password")) {
                    var6 = 18;
                }
                break;
            case 1322769895:
                if (var5.equals("watchedfields")) {
                    var6 = 26;
                }
                break;
            case 1425433203:
                if (var5.equals("sendOnlyUpdatedField")) {
                    var6 = 22;
                }
                break;
            case 2133441107:
                if (var5.equals("sendonlyupdatedfield")) {
                    var6 = 21;
                }
        }

        switch(var6) {
            case 0:
            case 1:
                return target.getConfiguration().getAccessToken();
            case 2:
            case 3:
                return target.isBridgeErrorHandler();
            case 4:
            case 5:
                return target.getConfiguration().getConsumerKey();
            case 6:
                return target.getConfiguration().getDelay();
            case 7:
            case 8:
                return target.getExceptionHandler();
            case 9:
            case 10:
                return target.getExchangePattern();
            case 11:
            case 12:
                return target.getConfiguration().getJiraUrl();
            case 13:
                return target.getJql();
            case 14:
            case 15:
                return target.isLazyStartProducer();
            case 16:
            case 17:
                return target.getMaxResults();
            case 18:
                return target.getConfiguration().getPassword();
            case 19:
            case 20:
                return target.getConfiguration().getPrivateKey();
            case 21:
            case 22:
                return target.isSendOnlyUpdatedField();
            case 23:
                return target.getConfiguration().getUsername();
            case 24:
            case 25:
                return target.getConfiguration().getVerificationCode();
            case 26:
            case 27:
                return target.getWatchedFields();
            default:
                return null;
        }
    }
}
