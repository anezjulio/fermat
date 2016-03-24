package com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.ReferenceWallet;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.dmp_module.wallet_manager.CantLoadWalletsException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.bitcoin_wallet.interfaces.BitcoinWalletManager;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantFindTransactionException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantLoadWalletException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantRegisterDebitException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.loss_protected_wallet.interfaces.BitcoinLossProtectedWalletManager;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.transfer_intra_wallet_users.exceptions.TransferIntraWalletUsersNotEnoughFundsException;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.transfer_intra_wallet_users.interfaces.TransferIntraWalletUsers;
import com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.database.TransferIntraWalletUsersDao;
import com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.enums.TransactionState;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.transfer_intra_wallet_users.exceptions.CantSendTransactionException;
import com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.exceptions.TransferIntraWalletUsersCantCancelTransactionException;
import com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.utils.BitcoinWalletTransactionWalletRecord;

import com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.exceptions.TransferIntraWalletUsersCantInsertRecordException;
import com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.utils.BitcoinLossProtectedWalletTransactionWalletRecord;
import com.bitdubai.fermat_cpp_plugin.layer.crypto_transaction.TransferIntraWalletUsers.bitdubai.version_1.utils.TransferIntraWalletUsersWrapper;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.util.UUID;

/**
 * Created by Joaquin Carrasquero on 18/03/16.
 */
public class TransferIntraWalletUsersModuleManager implements TransferIntraWalletUsers {


    private final BitcoinLossProtectedWalletManager bitcoinLossWalletManager;
    private final BitcoinWalletManager bitcoinWalletManager;
    private final ErrorManager errorManager;
    private final TransferIntraWalletUsersDao dao;


    public TransferIntraWalletUsersModuleManager(final BitcoinLossProtectedWalletManager bitcoinLossWalletManager,
                                                 final BitcoinWalletManager bitcoinWalletManager,
                                                 final ErrorManager errorManager,
                                                 final TransferIntraWalletUsersDao dao) {
        this.bitcoinLossWalletManager = bitcoinLossWalletManager;
        this.bitcoinWalletManager = bitcoinWalletManager;
        this.errorManager = errorManager;
        this.dao = dao;
    }

    //TODO: los try catch tenes que ponerlos englobando toda la funcion, me parece que para que quede mas claro tenes que hacer un
    //switch para descontar de la loss y otro switch para enviar a la bitcoin
    //si el primer bloque da error no seguis y avisas al usuario con el broadcaster, mas el envio del error, lo mismo si pasa algo en el segundo bloque
    //en el caso de q de error cuando le envias los btc a la otra wallet tenes que reversar la trasaccion en la loss eso ya hay un metodo
    //Lo primero que haces cuando entras al metodo send es guardar la transaccion en la base de datos de este plugin
    //si pasa el primer bloque le pones un estado, si pasa el segundo estado completado
    //si da error estado de error
    //Acordate que la transaccion tiene que tener un tipo Intra Wallets y la transaccion que se guarda en la loss lleva el Exchange rate en cero

    //hay q ver si no puede completar la transaccion de avisar con una notificacion
    @Override
    public void sendToWallet(
            String txHash,
            long cryptoAmount,
            String notes,
            Actors actortype,
            ReferenceWallet reference_wallet_sending,
            ReferenceWallet reference_wallet_receiving,
            String wallet_public_key_sending,
            String wallet_public_key_receiving,
            BlockchainNetworkType blockchainNetworkType) throws CantSendTransactionException, TransferIntraWalletUsersNotEnoughFundsException {


        Long balanceBeforeCredit = null;
        Long amountRecord = null;

        Long initialBalance = null;

        UUID id = UUID.randomUUID();

        try {
            //Chooses wallet to be credited.

            switch (reference_wallet_receiving) {

                case BASIC_WALLET_BITCOIN_WALLET:

                    //Consult current balance of the sending wallet

                    initialBalance = this.bitcoinLossWalletManager.loadWallet(wallet_public_key_sending).getBalance(BalanceType.AVAILABLE).getBalance(blockchainNetworkType);


                    //Checks the balance of the debiting wallet in order to decide if to continue or not.

                    if (initialBalance != null && initialBalance >= cryptoAmount) {


                        //Prepares the record to be used within transactions

                        BitcoinWalletTransactionWalletRecord bitcoinWalletTransactionWalletRecord = buildBitcoinWalletRecord(id,
                                null,
                                null,
                                cryptoAmount,
                                null,
                                notes,
                                System.currentTimeMillis(),
                                "",
                                "",
                                "",
                                actortype,
                                actortype,
                                blockchainNetworkType);

                        balanceBeforeCredit = this.bitcoinWalletManager.loadWallet(wallet_public_key_receiving).getBalance(BalanceType.AVAILABLE).getBalance(blockchainNetworkType);

                        //Checks that the current balance for the crediting wallet is not null in order to proceed.

                        if (balanceBeforeCredit != null) {


                            //Register the new transaction
                            dao.registerNewTransaction(id,
                                    txHash,
                                    cryptoAmount,
                                    notes,
                                    actortype,
                                    reference_wallet_sending,
                                    reference_wallet_receiving,
                                    wallet_public_key_sending,
                                    wallet_public_key_receiving,
                                    TransactionState.NEW,
                                    blockchainNetworkType);


                            this.bitcoinWalletManager.loadWallet(wallet_public_key_receiving).getBalance(BalanceType.BOOK).credit(bitcoinWalletTransactionWalletRecord);
                            this.bitcoinWalletManager.loadWallet(wallet_public_key_receiving).getBalance(BalanceType.AVAILABLE).credit(bitcoinWalletTransactionWalletRecord);

                        }


                        amountRecord = this.bitcoinWalletManager.loadWallet(wallet_public_key_receiving).getTransactionById(bitcoinWalletTransactionWalletRecord.getTransactionId()).getAmount();

                        //check if amount within DB record correspond with the amount sent,
                        // if it does, then it should debit it within the wallet.
                        //if it does not, then it will not continue.


                        if (amountRecord == cryptoAmount) {

                            //Proceeds to debit in the correct wallet
                            receivedToWallet(id,
                                    txHash,
                                    cryptoAmount,
                                    notes,
                                    actortype,
                                    reference_wallet_sending,
                                    reference_wallet_receiving,
                                    wallet_public_key_sending,
                                    wallet_public_key_receiving,
                                    blockchainNetworkType);

                        }//end if


                    } else {
                        //There are not enough funds to perform this transaction
                        throw new TransferIntraWalletUsersNotEnoughFundsException("There are not enough funds to perform this transaction", null, "", "NotEnoughFunds");
                    }


                    break;
                case BASIC_WALLET_DISCOUNT_WALLET:
                    break;
                case BASIC_WALLET_FIAT_WALLET:
                    break;
                case BASIC_WALLET_LOSS_PROTECTED_WALLET:


                    //consult current balance of the sending wallet


                    initialBalance = this.bitcoinWalletManager.loadWallet(wallet_public_key_sending).getBalance(BalanceType.AVAILABLE).getBalance(blockchainNetworkType);

                    //Checks the balance of the debiting wallet in order to decide if to continue or not.

                    if (initialBalance != null && initialBalance >= cryptoAmount) {

                        //Prepares the record to be used within transactions

                        BitcoinLossProtectedWalletTransactionWalletRecord bitcoinLossProtectedWalletTransactionWalletRecord2 = buildLossWalletRecord(id,
                                null,
                                null,
                                cryptoAmount,
                                null,
                                notes,
                                System.currentTimeMillis(),
                                "",
                                "",
                                "",
                                actortype,
                                actortype,
                                blockchainNetworkType);


                        balanceBeforeCredit = this.bitcoinLossWalletManager.loadWallet(wallet_public_key_receiving).getBalance(BalanceType.AVAILABLE).getBalance(blockchainNetworkType);

                        //Checks that the current balance for the crediting wallet is not null in order to proceed.


                        if (balanceBeforeCredit != null) {


                            dao.registerNewTransaction(id,
                                    txHash,
                                    cryptoAmount,
                                    notes,
                                    actortype,
                                    reference_wallet_sending,
                                    reference_wallet_receiving,
                                    wallet_public_key_sending,
                                    wallet_public_key_receiving,
                                    TransactionState.NEW,
                                    blockchainNetworkType);


                            this.bitcoinLossWalletManager.loadWallet(wallet_public_key_receiving).getBalance(BalanceType.BOOK).credit(bitcoinLossProtectedWalletTransactionWalletRecord2);
                            this.bitcoinLossWalletManager.loadWallet(wallet_public_key_receiving).getBalance(BalanceType.AVAILABLE).credit(bitcoinLossProtectedWalletTransactionWalletRecord2);

                        }


                        amountRecord = this.bitcoinLossWalletManager.loadWallet(wallet_public_key_receiving).getTransactionById(bitcoinLossProtectedWalletTransactionWalletRecord2.getTransactionId()).getAmount();


                        //check if amount within DB record correspond with the amount sent,
                        // if it does, then it should debit it within the wallet.
                        //if it does not, then it will not continue.


                        if (amountRecord == cryptoAmount) {
                            //Proceeds to debit in the correct wallet
                            receivedToWallet(id,
                                    txHash,
                                    cryptoAmount,
                                    notes,
                                    actortype,
                                    reference_wallet_sending,
                                    reference_wallet_receiving,
                                    wallet_public_key_sending,
                                    wallet_public_key_receiving,
                                    blockchainNetworkType);

                        }//end if


                    } else {
                        //There are not enough funds to perform this transaction
                        throw new TransferIntraWalletUsersNotEnoughFundsException("There are not enough funds to perform this transaction", null, "", "NotEnoughFunds");
                    }


                    break;
                case COMPOSITE_WALLET_MULTI_ACCOUNT:
                    break;


            }

        } catch (CantFindTransactionException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (CantLoadWalletException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (CantRegisterCreditException e) {
            //Update transaction to error state
            try {
                TransferIntraWalletUsersWrapper transferIntraWalletUsersWrapper = dao.getTransaction(id);
                //Set transaction to error state
                dao.setToError(transferIntraWalletUsersWrapper);
            } catch (CantLoadTableToMemoryException e1) {
                e1.printStackTrace();
                throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
            } catch (InvalidParameterException e1) {
                e1.printStackTrace();
                throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
            } catch (TransferIntraWalletUsersCantCancelTransactionException e1) {
                e1.printStackTrace();
                throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
            }


            e.printStackTrace();
        } catch (TransferIntraWalletUsersCantInsertRecordException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (CantCalculateBalanceException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (CantLoadWalletsException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        }

    }


    public void receivedToWallet(UUID id,
                                 String txHash,
                                 long cryptoAmount,
                                 String notes,
                                 Actors actortype,
                                 ReferenceWallet reference_wallet_sending,
                                 ReferenceWallet reference_wallet_receiving,
                                 String wallet_public_key_sending,
                                 String wallet_public_key_receiving,
                                 BlockchainNetworkType blockchainNetworkType) throws CantSendTransactionException {

        try {
            //checks what is the corresponding wallet to be debited.
            switch (reference_wallet_sending) {

                case BASIC_WALLET_BITCOIN_WALLET:

                    //Prepares the record to be used within transactions

                    BitcoinWalletTransactionWalletRecord bitcoinWalletTransactionWalletRecord2 = buildBitcoinWalletRecord(id,
                            null,
                            null,
                            cryptoAmount,
                            null,
                            notes,
                            System.currentTimeMillis(),
                            "",
                            "",
                            "",
                            actortype,
                            actortype,
                            blockchainNetworkType);


                    this.bitcoinWalletManager.loadWallet(wallet_public_key_sending).getBalance(BalanceType.AVAILABLE).debit(bitcoinWalletTransactionWalletRecord2);
                    this.bitcoinWalletManager.loadWallet(wallet_public_key_sending).getBalance(BalanceType.BOOK).debit(bitcoinWalletTransactionWalletRecord2);

                    //Change record state to completed.

                    TransferIntraWalletUsersWrapper transactionWrapper = dao.getTransaction(id);
                    dao.setToCompleted(transactionWrapper);


                    break;
                case BASIC_WALLET_DISCOUNT_WALLET:
                    break;
                case BASIC_WALLET_FIAT_WALLET:
                    break;
                case BASIC_WALLET_LOSS_PROTECTED_WALLET:
                    //Prepares the record to be used within transactions

                    BitcoinLossProtectedWalletTransactionWalletRecord bitcoinLossProtectedWalletTransactionWalletRecord = buildLossWalletRecord(id,
                            null,
                            null,
                            cryptoAmount,
                            null,
                            notes,
                            System.currentTimeMillis(),
                            "",
                            "",
                            "",
                            actortype,
                            actortype,
                            blockchainNetworkType);


                    this.bitcoinLossWalletManager.loadWallet(wallet_public_key_sending).getBalance(BalanceType.AVAILABLE).debit(bitcoinLossProtectedWalletTransactionWalletRecord);
                    this.bitcoinLossWalletManager.loadWallet(wallet_public_key_sending).getBalance(BalanceType.BOOK).debit(bitcoinLossProtectedWalletTransactionWalletRecord);


                    //Changes record state to completed.

                    TransferIntraWalletUsersWrapper transactionWrapper2 = dao.getTransaction(id);
                    dao.setToCompleted(transactionWrapper2);


                    break;


            }
        } catch (CantLoadTableToMemoryException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (CantRegisterDebitException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (TransferIntraWalletUsersCantCancelTransactionException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (CantLoadWalletException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        } catch (CantLoadWalletsException e) {
            e.printStackTrace();
            throw new CantSendTransactionException("I could not send the transaction", e, "TransferIntraWalletUsersModuleManager", "unknown reason");
        }


    }


    public BitcoinLossProtectedWalletTransactionWalletRecord buildLossWalletRecord(UUID transactionId,
                                                                                   CryptoAddress addressFrom,
                                                                                   UUID requestId,
                                                                                   long amount,
                                                                                   CryptoAddress addressTo,
                                                                                   String memo,
                                                                                   long timestamp,
                                                                                   String transactionHash,
                                                                                   String actorFromPublicKey,
                                                                                   String actorToPublicKey,
                                                                                   Actors actorToType,
                                                                                   Actors actorFromType,
                                                                                   com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType blockchainNetworkType) {


        //UUID pluginId = UUID.randomUUID();

        BitcoinLossProtectedWalletTransactionWalletRecord bitcoinLossProtectedWalletTransactionRecord = new BitcoinLossProtectedWalletTransactionWalletRecord(transactionId,
                addressFrom,
                requestId,
                amount,
                addressTo,
                memo,
                timestamp,
                transactionHash,
                actorFromPublicKey,
                actorToPublicKey,
                actorToType,
                actorFromType,
                blockchainNetworkType);

//        BitcoinLossProtectedWalletTransactionWalletRecord bitcoinLossProtectedWalletTransactionRecord = new BitcoinLossProtectedWalletTransactionWalletRecord(pluginId,
//                null,
//                null,
//                cryptoAmount,
//                null,
//                notes,
//                System.currentTimeMillis(),
//                "",
//                "",
//                "",
//                actorType,
//                actorType,
//                blockchainNetworkType);


        return bitcoinLossProtectedWalletTransactionRecord;

    }


    public BitcoinWalletTransactionWalletRecord buildBitcoinWalletRecord(UUID transactionId,
                                                                         CryptoAddress addressFrom,
                                                                         UUID requestId,
                                                                         long amount,
                                                                         CryptoAddress addressTo,
                                                                         String memo,
                                                                         long timestamp,
                                                                         String transactionHash,
                                                                         String actorFromPublicKey,
                                                                         String actorToPublicKey,
                                                                         Actors actorToType,
                                                                         Actors actorFromType,
                                                                         com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType blockchainNetworkType) {


        //UUID pluginId = UUID.randomUUID();


        BitcoinWalletTransactionWalletRecord bitcoinLossProtectedWalletTransactionRecord = new BitcoinWalletTransactionWalletRecord(transactionId,
                addressFrom,
                requestId,
                amount,
                addressTo,
                memo,
                timestamp,
                transactionHash,
                actorFromPublicKey,
                actorToPublicKey,
                actorToType,
                actorFromType,
                blockchainNetworkType);


//        BitcoinWalletTransactionWalletRecord bitcoinLossProtectedWalletTransactionRecord = new BitcoinWalletTransactionWalletRecord(pluginId,
//                null,
//                null,
//                cryptoAmount,
//                null,
//                notes,
//                System.currentTimeMillis(),
//                "",
//                "",
//                "",
//                actorType,
//                actorType,
//                blockchainNetworkType);


        return bitcoinLossProtectedWalletTransactionRecord;

    }


}
