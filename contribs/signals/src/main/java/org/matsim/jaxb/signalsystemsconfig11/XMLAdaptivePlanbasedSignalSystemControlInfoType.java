//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.09.20 at 07:22:03 PM MESZ 
//


package org.matsim.jaxb.signalsystemsconfig11;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * If there is an adaptive control mechanism implemented its id or name
 * 		can be set in the element of this type. For plan based adaptive control strategies
 * 		one or more plans can be added as well.
 * 
 * <p>Java class for adaptivePlanbasedSignalSystemControlInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adaptivePlanbasedSignalSystemControlInfoType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.matsim.org/files/dtd}adaptiveSignalSystemControlInfoType">
 *       &lt;sequence>
 *         &lt;element name="signalSystemPlan" type="{http://www.matsim.org/files/dtd}signalSystemPlanType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adaptivePlanbasedSignalSystemControlInfoType", propOrder = {
    "signalSystemPlan"
})
public class XMLAdaptivePlanbasedSignalSystemControlInfoType
    extends XMLAdaptiveSignalSystemControlInfoType
{

    protected List<XMLSignalSystemPlanType> signalSystemPlan;

    /**
     * Gets the value of the signalSystemPlan property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signalSystemPlan property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignalSystemPlan().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLSignalSystemPlanType }
     * 
     * 
     */
    public List<XMLSignalSystemPlanType> getSignalSystemPlan() {
        if (signalSystemPlan == null) {
            signalSystemPlan = new ArrayList<XMLSignalSystemPlanType>();
        }
        return this.signalSystemPlan;
    }

}
