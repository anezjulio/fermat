package com.bitdubai.fermat_bch_plugin_layer.watch_only_vault.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.util.XMLParser;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantPersistFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces.BitcoinNetworkManager;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.exceptions.CantGetExtendedPublicKeyException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.classes.HierarchyAccount.HierarchyAccount;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.classes.HierarchyAccount.HierarchyAccountType;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.exceptions.GetNewCryptoAddressException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.ExtendedPublicKey;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.exceptions.CantInitializeWatchOnlyVaultException;
import com.bitdubai.fermat_bch_plugin_layer.watch_only_vault.developer.bitdubai.version_1.database.BitcoinWatchOnlyCryptoVaultDao;
import com.bitdubai.fermat_bch_plugin_layer.watch_only_vault.developer.bitdubai.version_1.exceptions.CantExecuteDatabaseOperationException;
import com.bitdubai.fermat_bch_plugin_layer.watch_only_vault.developer.bitdubai.version_1.exceptions.CantInitializeBitcoinWatchOnlyCryptoVaultDatabaseException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Created by rodrigo on 12/30/15.
 */
public class BitcoinWatchOnlyVaultManager {
    /**
     * BitcoinWatchOnlyVaultManager variables
     */
    WatchOnlyVaultExtendedPublicKey watchOnlyVaultExtendedPublicKey;
    final String DIRECTORY_NAME = "WatchOnlyVault";
    BitcoinWatchOnlyCryptoVaultDao bitcoinWatchOnlyCryptoVaultDao;
    VaultKeyHierarchyGenerator generator;

    /**
     * Platform variables
     */
    ErrorManager errorManager;
    PluginDatabaseSystem pluginDatabaseSystem;
    PluginFileSystem pluginFileSystem;
    BitcoinNetworkManager bitcoinNetworkManager;
    UUID pluginId;


    /**
     * Constructor
     * @param errorManager
     * @param pluginDatabaseSystem
     * @param pluginFileSystem
     * @param bitcoinNetworkManager
     */
    public BitcoinWatchOnlyVaultManager(ErrorManager errorManager, PluginDatabaseSystem pluginDatabaseSystem, PluginFileSystem pluginFileSystem, BitcoinNetworkManager bitcoinNetworkManager, UUID pluginId) {
        this.errorManager = errorManager;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginFileSystem = pluginFileSystem;
        this.bitcoinNetworkManager = bitcoinNetworkManager;
        this.pluginId = pluginId;


        try{
            /**
             * I will get all stored Hierarchy Accounts that may belong to multiple redeem points
             */
            for (HierarchyAccount hierarchyAccount : getDao().getHierarchyAccounts()){
                /**
                 * for each account, I will load the ExtendedPublicKey
                 */
                WatchOnlyVaultExtendedPublicKey watchOnlyVaultExtendedPublicKey = loadExtendedPublicKey(DIRECTORY_NAME, hierarchyAccount.getDescription());
                DeterministicKey rootKey = getMasterPublicKey(watchOnlyVaultExtendedPublicKey);
                /**
                 * and will generate the KeyHierarchy from this Extended Key.
                 */
                generator = new VaultKeyHierarchyGenerator(rootKey, hierarchyAccount, this.pluginDatabaseSystem, this.bitcoinNetworkManager, this.pluginId);
                /**
                 * the generation process will go in a new thread and will be completed once the keys are passed to the CryptoNetwork
                 */
                new Thread(generator).start();
            }
        } catch (CantExecuteDatabaseOperationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CantCreateFileException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will initialize the Vault by deriving the keys, starting the agents and start monitoring the network
     * @param extendedPublicKey
     * @throws CantInitializeWatchOnlyVaultException
     */
    public void initialize(ExtendedPublicKey extendedPublicKey) throws CantInitializeWatchOnlyVaultException {
        if (extendedPublicKey == null)
            throw new CantInitializeWatchOnlyVaultException(CantInitializeWatchOnlyVaultException.DEFAULT_MESSAGE, null, "Extended public Key received is null. Can't go on.", null);



        /**
         * I will get the WatchOnlyVaultExtendedPublicKey by storing the received extendedPublicKey
         */
        try {
            watchOnlyVaultExtendedPublicKey = getExtendedPublicKey(extendedPublicKey);
        } catch (CantGetExtendedPublicKeyException e) {
            throw new CantInitializeWatchOnlyVaultException(CantInitializeWatchOnlyVaultException.DEFAULT_MESSAGE, e, "Error loading or saving the extended key", "IO issue");
        }

        /**
         * If this Account Key was added before, I can't continue because I might not have the same Key
         */
        try {
            if (getDao().isExistingRedeemPoint(watchOnlyVaultExtendedPublicKey.getExtendedPublicKey().getActorPublicKey()));
            throw new CantInitializeWatchOnlyVaultException(CantInitializeWatchOnlyVaultException.DEFAULT_MESSAGE, null, "A Hierarchy Account already exists for this public Key.", null );
        } catch (CantExecuteDatabaseOperationException e) {
            /**
             * If there was a database error, I will continue.
             */
            e.printStackTrace();
        }

        HierarchyAccount hierarchyAccount;
        try {
            hierarchyAccount = createNewHierarchyAccount(watchOnlyVaultExtendedPublicKey.getExtendedPublicKey().getActorPublicKey());
        } catch (CantExecuteDatabaseOperationException e) {
            throw new CantInitializeWatchOnlyVaultException(CantInitializeWatchOnlyVaultException.DEFAULT_MESSAGE, e, "Hierarchy Account could not be added to the database.", "database issue" );
        }

        DeterministicKey masterPublicKey = getMasterPublicKey(watchOnlyVaultExtendedPublicKey);

        VaultKeyHierarchyGenerator generator = new VaultKeyHierarchyGenerator(masterPublicKey, hierarchyAccount, this.pluginDatabaseSystem, this.bitcoinNetworkManager, this.pluginId);
        new Thread(generator).start();
    }


    /**
     * starts in a new thread the process that will generate the Key hierarchy.
     * Once created will start the KeyHierarchy Maintainer which will derive all needed keys and
     * pass them to the crypto network to start the monitoring.
     * @param generator
     */
    private void startHierarchyGenerationProcess(VaultKeyHierarchyGenerator generator){
        new Thread(generator).start();
    }

    /**
     * Will deserialize the public Key that we just recieved to get the master Public Key.
     * @param watchOnlyVaultExtendedPublicKey
     * @return
     */
    private DeterministicKey getMasterPublicKey(WatchOnlyVaultExtendedPublicKey watchOnlyVaultExtendedPublicKey) {
        byte[] pubKeyBytes, chainCode;

        pubKeyBytes = watchOnlyVaultExtendedPublicKey.getExtendedPublicKey().getPubKeyBytes();
        chainCode = watchOnlyVaultExtendedPublicKey.getExtendedPublicKey().getChainCode();

        final DeterministicKey watchPubKeyAccountZero = HDKeyDerivation.createMasterPubKeyFromBytes(pubKeyBytes, chainCode);
        return watchPubKeyAccountZero;
    }

    /**
     * Generates a new Hierarchy Account in the database
     * @param actorPublicKey
     * @return
     */
    private HierarchyAccount createNewHierarchyAccount(String actorPublicKey) throws CantExecuteDatabaseOperationException {
        int id = getDao().getNextAvailableHierarchyAccountId();

        HierarchyAccount hierarchyAccount = new HierarchyAccount(id, actorPublicKey, HierarchyAccountType.REDEEMPOINT_ACCOUNT);
        getDao().addNewHierarchyAccount(hierarchyAccount);

        return hierarchyAccount;
    }

    /**
     * Instantiates the BitcoinWatchOnlyCryptoVaultDao class that will access the database
     * @return
     */
    private BitcoinWatchOnlyCryptoVaultDao getDao(){
        if (bitcoinWatchOnlyCryptoVaultDao == null) {
            try {
                bitcoinWatchOnlyCryptoVaultDao = new BitcoinWatchOnlyCryptoVaultDao(this.pluginDatabaseSystem, this.pluginId);
            } catch (CantInitializeBitcoinWatchOnlyCryptoVaultDatabaseException e) {
                return bitcoinWatchOnlyCryptoVaultDao;
            }
        }
            return bitcoinWatchOnlyCryptoVaultDao;
    }



    /**
     * Will store in file and instantiate the watchOnlyVaultExtendedPublicKey class
     * @param extendedPublicKey
     * @return
     */
    private WatchOnlyVaultExtendedPublicKey getExtendedPublicKey(ExtendedPublicKey extendedPublicKey) throws CantGetExtendedPublicKeyException {
        WatchOnlyVaultExtendedPublicKey watchOnlyVaultExtendedPublicKey = new WatchOnlyVaultExtendedPublicKey(extendedPublicKey.getActorPublicKey(), DIRECTORY_NAME, extendedPublicKey);

        try {
            storeExtendedPublicKey(watchOnlyVaultExtendedPublicKey);
            return loadExtendedPublicKey(watchOnlyVaultExtendedPublicKey.DIRECTORY_NAME, watchOnlyVaultExtendedPublicKey.FILE_NAME);
        } catch (Exception e) {
            throw new CantGetExtendedPublicKeyException(CantGetExtendedPublicKeyException.DEFAULT_MESSAGE, e, "Error loading or saving from disk the Extended Public JKey", "IO failure");
        }
    }

    /**
     * Loads from file the WatchOnlyVaultExtendedPublicKey assigned to the vault. (if any)
     * @param directory_name
     * @param file_name
     * @return
     */
    private WatchOnlyVaultExtendedPublicKey loadExtendedPublicKey(String directory_name, String file_name) throws FileNotFoundException, CantCreateFileException {
        /**
         * Loads the file ans instantiate the WatchOnlyVaultExtendedPublicKey class.
         */
        PluginTextFile textFile = pluginFileSystem.getTextFile(this.pluginId, DIRECTORY_NAME, watchOnlyVaultExtendedPublicKey.getFILE_NAME(), FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);

        String fileContent = textFile.getContent();
        WatchOnlyVaultExtendedPublicKey watchOnlyVaultExtendedPublicKey = (WatchOnlyVaultExtendedPublicKey) XMLParser.parseXML(fileContent, WatchOnlyVaultExtendedPublicKey.class);

        return watchOnlyVaultExtendedPublicKey;
    }

    private void storeExtendedPublicKey(WatchOnlyVaultExtendedPublicKey watchOnlyVaultExtendedPublicKey) throws CantCreateFileException, CantPersistFileException {
        /**
         * Create the file and set it content.
         */
        PluginTextFile textFile = pluginFileSystem.createTextFile(this.pluginId, DIRECTORY_NAME, watchOnlyVaultExtendedPublicKey.getFILE_NAME(), FilePrivacy.PRIVATE, FileLifeSpan.PERMANENT);

        String fileContent = getFileContent(watchOnlyVaultExtendedPublicKey);
        textFile.setContent(fileContent);
        textFile.persistToMedia();
    }

    /**
     * transform into XML the extendedPublicKey received class.
     * @param extendedPublicKey
     * @return
     */
    private String getFileContent(WatchOnlyVaultExtendedPublicKey extendedPublicKey) {
        return XMLParser.parseObject(extendedPublicKey);
    }

    /**
     * Generates a Crypto Address for the specified Network.
     * @param blockchainNetworkType DEFAULT if null value is passed.
     * @return the newly generated crypto Address.
     */
    public CryptoAddress getCryptoAddress(@Nullable BlockchainNetworkType blockchainNetworkType) throws GetNewCryptoAddressException {
        /**
         * I create the account manually instead of getting it from the database because this method always returns addresses
         * from the asset vault account with Id 0.
         */
        com.bitdubai.fermat_bch_api.layer.crypto_vault.classes.HierarchyAccount.HierarchyAccount vaultAccount = new com.bitdubai.fermat_bch_api.layer.crypto_vault.classes.HierarchyAccount.HierarchyAccount(0, "Asset Vault account", HierarchyAccountType.MASTER_ACCOUNT);
        return generator.getVaultKeyHierarchy().getBitcoinAddress(blockchainNetworkType, vaultAccount);
    }
}
