package com.bitdubai.fermat_cbp_api.layer.cbp_negotiation.customer_broker_sale.interfaces;

import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationStatus;
import com.bitdubai.fermat_cbp_api.all_definition.identity.ActorIdentity;
import com.bitdubai.fermat_cbp_api.layer.cbp_negotiation.customer_broker_sale.exceptions.CantCreateCustomerBrokerSaleException;

import java.util.Collection;

/**
 * Created by jorge on 10-10-2015.
 */
public interface CustomerBrokerSaleManager {

    CustomerBrokerSale createNegotiation() throws CantCreateCustomerBrokerSaleException;
    void cancelNegotiation(CustomerBrokerSale negotiation);
    void closeNegotiation(CustomerBrokerSale negotiation);

    Collection<CustomerBrokerSale> getNegotiations();
    Collection<CustomerBrokerSale> getNegotiations(NegotiationStatus status);
    Collection<CustomerBrokerSale> getNegotiationsByCustomer(ActorIdentity customer);
    Collection<CustomerBrokerSale> getNegotiationsByBroker(ActorIdentity broker);
}