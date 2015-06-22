package com.bitdubai.fermat_api.layer.dmp_niche_wallet_type.crypto_wallet.exceptions;

/**
 * The Class <code>com.bitdubai.fermat_api.layer.dmp_niche_wallet_type.crypto_wallet.exceptions.CantDeleteWalletContactException</code>
 * is thrown when an error occurs trying to delete any contact from a wallet
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 09/06/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CantDeleteWalletContactException extends Exception {

    /**
     * Constructor
     */
    public CantDeleteWalletContactException(){
        super();
    }

    /**
     * Constructor whit parameter
     * @param msg
     */
    public CantDeleteWalletContactException(String msg){
        super(msg);
    }
}
