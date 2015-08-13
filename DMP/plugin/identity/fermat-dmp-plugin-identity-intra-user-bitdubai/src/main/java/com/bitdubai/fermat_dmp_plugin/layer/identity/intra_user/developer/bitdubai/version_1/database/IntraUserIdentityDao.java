package com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.database;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.DeviceDirectory;
import com.bitdubai.fermat_api.layer.dmp_identity.intra_user.interfaces.IntraUserIdentity;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginBinaryFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantLoadFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantPersistFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.exceptions.CantExecuteDatabaseOperationException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.exceptions.CantGetIntraUserIdentitiesException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.exceptions.CantGetIntraUserIdentityPrivateKeyException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.exceptions.CantGetIntraUserIdentityProfileImageException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.exceptions.CantInitializeIntraUserIdentityDatabaseException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.exceptions.CantPersistPrivateKeyException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.exceptions.CantPersistProfileImageException;
import com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.structure.IntraUserIdentityIdentity;
import com.bitdubai.fermat_pip_api.layer.pip_identity.developer.exceptions.CantCreateNewDeveloperException;
import com.bitdubai.fermat_pip_api.layer.pip_identity.developer.exceptions.CantGetUserDeveloperIdentitiesException;
import com.bitdubai.fermat_pip_api.layer.pip_user.device_user.interfaces.DeviceUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Class <code>com.bitdubai.fermat_dmp_plugin.layer.identity.intra_user.developer.bitdubai.version_1.database.IntraUserIdentityDao</code>
 * has all methods related with database access.<p/>
 * <p/>
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 07/08/15.
 * Modified by Natalia on 11/08/2015
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class IntraUserIdentityDao implements DealsWithPluginDatabaseSystem {

    /**
     * Represent the Plugin Database.
     */

    String INTRA_USERS_PRIVATE_KEYS_FILE_NAME = "intraUserIdentityPrivateKey";
    String INTRA_USERS_PROFILE_IMAGE_FILE_NAME = "intraUserIdentityProfileImage";
    private PluginDatabaseSystem pluginDatabaseSystem;

    private PluginFileSystem pluginFileSystem;

    private UUID pluginId;

    /**
     * Constructor with parameters
     *
     * @param pluginDatabaseSystem DealsWithPluginDatabaseSystem
     */

    public IntraUserIdentityDao(PluginDatabaseSystem pluginDatabaseSystem,PluginFileSystem pluginFileSystem, UUID pluginId) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginFileSystem = pluginFileSystem;
        this.pluginId = pluginId;
    }

    /**
     * Represent de Database where i will be working with
     */
    Database database;

    /**
     * This method open or creates the database i'll be working with     *

     * @throws CantInitializeIntraUserIdentityDatabaseException
     */
    public void initializeDatabase() throws CantInitializeIntraUserIdentityDatabaseException {
        try {
             /*
              * Open new database connection
              */
            database = this.pluginDatabaseSystem.openDatabase(this.pluginId, IntraUserIdentityDatabaseConstants.INTRA_USER_DATABASE_NAME);
            database.closeDatabase();
        } catch (CantOpenDatabaseException cantOpenDatabaseException) {
            throw new CantInitializeIntraUserIdentityDatabaseException(CantInitializeIntraUserIdentityDatabaseException.DEFAULT_MESSAGE, cantOpenDatabaseException, "", "Exception not handled by the plugin, there is a problem and i cannot open the database.");
        } catch (DatabaseNotFoundException e) {
             /*
              * The database no exist may be the first time the plugin is running on this device,
              * We need to create the new database
              */
            IntraUserIdentityDatabaseFactory databaseFactory = new IntraUserIdentityDatabaseFactory(pluginDatabaseSystem);
            try {
                  /*
                   * We create the new database
                   */
                database = databaseFactory.createDatabase(this.pluginId, IntraUserIdentityDatabaseConstants.INTRA_USER_DATABASE_NAME);
                database.closeDatabase();
            } catch (CantCreateDatabaseException cantCreateDatabaseException) {
                  /*
                   * The database cannot be created. I can not handle this situation.
                   */
                throw new CantInitializeIntraUserIdentityDatabaseException(CantInitializeIntraUserIdentityDatabaseException.DEFAULT_MESSAGE, cantCreateDatabaseException, "", "There is a problem and i cannot create the database.");
            }
        }
    }

    public void createNewUser (String alias, String publicKey,String privateKey, DeviceUser deviceUser,byte[] profileImage) throws CantCreateNewDeveloperException {

        try {


            if (aliasExists (alias)) {

                throw new CantCreateNewDeveloperException ("Cant create new developer, alias exists.", "Translator Identity", "Cant create new developer, alias exists.");
            }

            DatabaseTable table = this.database.getTable(IntraUserIdentityDatabaseConstants.INTRA_USER_TABLE_NAME);
            DatabaseTableRecord record = table.getEmptyRecord();

            record.setStringValue(IntraUserIdentityDatabaseConstants.INTRA_USER_INTRA_USER_PUBLIC_KEY_COLUMN_NAME, publicKey);
            record.setStringValue(IntraUserIdentityDatabaseConstants.INTRA_USER_ALIAS_COLUMN_NAME,alias );
            record.setStringValue(IntraUserIdentityDatabaseConstants.INTRA_USER_DEVICE_USER_PUBLIC_KEY_COLUMN_NAME, deviceUser.getPublicKey());

            table.insertRecord(record);

           /**
             * Persist private key on a file
             */
            persistNewUserPrivateKeysFile(publicKey, privateKey);

            /**
             * Persist profile image on a file
             */
            persistNewUserProfileImage(publicKey, profileImage);

            database.closeDatabase();

        } catch (CantInsertRecordException e){
            database.closeDatabase();
            // Cant insert record.
            throw new CantCreateNewDeveloperException (e.getMessage(), e, "Translator Identity", "Cant create new developer, insert database problems.");

        } catch (CantPersistPrivateKeyException e){
            database.closeDatabase();
            // Cant insert record.
            throw new CantCreateNewDeveloperException (e.getMessage(), e, "Translator Identity", "Cant create new developer,persist private key error.");

        } catch (Exception e) {
            database.closeDatabase();
            // Failure unknown.
            throw new CantCreateNewDeveloperException (e.getMessage(), e, "Translator Identity", "Cant create new developer, unknown failure.");
        }


    }


    public List<IntraUserIdentity> getIntraUserFromCurrentDeviceUser (DeviceUser deviceUser) throws CantGetIntraUserIdentitiesException {


        // Setup method.
        List<IntraUserIdentity> list = new ArrayList<IntraUserIdentity>(); // Intra User list.
        DatabaseTable table; // Intra User table.

        // Get Intra Users identities list.
        try {

            /**
             * 1) Get the table.
             */
            table = this.database.getTable (IntraUserIdentityDatabaseConstants.INTRA_USER_TABLE_NAME);

            if (table == null) {
                /**
                 * Table not found.
                 */
                throw new CantGetUserDeveloperIdentitiesException ("Cant get intra user identity list, table not found.", "Plugin Identity", "Cant get Intra User identity list, table not found.");
            }


            // 2) Find the developers.
            table.setStringFilter(IntraUserIdentityDatabaseConstants.INTRA_USER_DEVICE_USER_PUBLIC_KEY_COLUMN_NAME, deviceUser.getPublicKey(), DatabaseFilterType.EQUAL);
            table.loadToMemory();


            // 3) Get developers.
            for (DatabaseTableRecord record : table.getRecords ()) {

                // Add records to list.
                list.add(new IntraUserIdentityIdentity(record.getStringValue(IntraUserIdentityDatabaseConstants.INTRA_USER_ALIAS_COLUMN_NAME),
                        record.getStringValue (IntraUserIdentityDatabaseConstants.INTRA_USER_DEVICE_USER_PUBLIC_KEY_COLUMN_NAME),
                        getIntraUserIdentiyPrivateKey(record.getStringValue(IntraUserIdentityDatabaseConstants.INTRA_USER_DEVICE_USER_PUBLIC_KEY_COLUMN_NAME)),
                        getIntraUserProfileImagePrivateKey(record.getStringValue(IntraUserIdentityDatabaseConstants.INTRA_USER_DEVICE_USER_PUBLIC_KEY_COLUMN_NAME))));
            }

            database.closeDatabase();
        }
        catch (CantLoadTableToMemoryException e) {
            database.closeDatabase();
            throw new CantGetIntraUserIdentitiesException(e.getMessage(), e, "Translator Identity", "Cant load " + IntraUserIdentityDatabaseConstants.INTRA_USER_TABLE_NAME + " table in memory.");
        }
        catch (CantGetIntraUserIdentityPrivateKeyException e) {
            database.closeDatabase();
            // Failure unknown.
            throw new CantGetIntraUserIdentitiesException (e.getMessage(), e, "Translator Identity", "Can't get private key.");

        } catch (Exception e) {
            database.closeDatabase();
            throw new CantGetIntraUserIdentitiesException (e.getMessage(), FermatException.wrapException(e), "Translator Identity", "Cant get developer identity list, unknown failure.");
        }


        // Return the list values.
        return list;
    }



    /**
     * DealsWithPluginDatabaseSystem Interface implementation.
     */
    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
    }

    /**
     * Private Methods
     */


    private Database openDatabase() throws CantExecuteDatabaseOperationException {
        try {
            return pluginDatabaseSystem.openDatabase(pluginId, IntraUserIdentityDatabaseConstants.INTRA_USER_DATABASE_NAME);
        } catch (CantOpenDatabaseException | DatabaseNotFoundException exception) {
            throw  new CantExecuteDatabaseOperationException("ERROR OPEN DATABASE",exception,"", "Error in database plugin.");
        }
    }

    private void  persistNewUserPrivateKeysFile(String publicKey,String privateKey) throws CantPersistPrivateKeyException {
        try {
            PluginTextFile file = this.pluginFileSystem.createTextFile(pluginId,
                    DeviceDirectory.LOCAL_USERS.getName(),
                    INTRA_USERS_PRIVATE_KEYS_FILE_NAME + "_" + publicKey,
                    FilePrivacy.PRIVATE,
                    FileLifeSpan.PERMANENT
            );

            file.setContent(privateKey);

            file.persistToMedia();
        } catch (CantPersistFileException e) {
            throw new CantPersistPrivateKeyException("CAN'T PERSIST PRIVATE KEY ", e, "Error persist file.", null);

        } catch (CantCreateFileException e) {
            throw new CantPersistPrivateKeyException("CAN'T PERSIST PRIVATE KEY ", e, "Error creating file.", null);
        }
    }


    private void  persistNewUserProfileImage(String publicKey,byte[] profileImage) throws CantPersistProfileImageException {
        try {
            PluginBinaryFile file = this.pluginFileSystem.createBinaryFile(pluginId,
                    DeviceDirectory.LOCAL_USERS.getName(),
                    INTRA_USERS_PROFILE_IMAGE_FILE_NAME + "_" + publicKey,
                    FilePrivacy.PRIVATE,
                    FileLifeSpan.PERMANENT
            );

            file.setContent(profileImage);

            file.persistToMedia();
        } catch (CantPersistFileException e) {
            throw new CantPersistProfileImageException("CAN'T PERSIST PROFILE IMAGE ", e, "Error persist file.", null);

        } catch (CantCreateFileException e) {
            throw new CantPersistProfileImageException("CAN'T PERSIST PROFILE IMAGE ", e, "Error creating file.", null);
        }
    }


    public String getIntraUserIdentiyPrivateKey(String publicKey) throws CantGetIntraUserIdentityPrivateKeyException {
        String privateKey = "";
        try {
            PluginTextFile file = this.pluginFileSystem.getTextFile(pluginId,
                    DeviceDirectory.LOCAL_USERS.getName(),
                    INTRA_USERS_PRIVATE_KEYS_FILE_NAME + "_" + publicKey,
                    FilePrivacy.PRIVATE,
                    FileLifeSpan.PERMANENT
            );


            file.loadFromMedia();

            privateKey = file.getContent();

        } catch (CantLoadFileException e) {
            throw new CantGetIntraUserIdentityPrivateKeyException("CAN'T GET PRIVATE KEY ", e, "Error loaded file.", null);

        }
        catch (FileNotFoundException |CantCreateFileException e) {
            throw new CantGetIntraUserIdentityPrivateKeyException("CAN'T GET PRIVATE KEY ", e, "Error getting developer identity private keys file.", null);
        }

        return privateKey;
    }

    public byte[] getIntraUserProfileImagePrivateKey(String publicKey) throws CantGetIntraUserIdentityProfileImageException {
        byte[] profileImage;
        try {
            PluginBinaryFile file = this.pluginFileSystem.getBinaryFile(pluginId,
                    DeviceDirectory.LOCAL_USERS.getName(),
                    INTRA_USERS_PROFILE_IMAGE_FILE_NAME + "_" + publicKey,
                    FilePrivacy.PRIVATE,
                    FileLifeSpan.PERMANENT
            );


            file.loadFromMedia();

            profileImage = file.getContent();

        } catch (CantLoadFileException e) {
            throw new CantGetIntraUserIdentityProfileImageException("CAN'T GET PRIVATE KEY ", e, "Error loaded file.", null);

        }
        catch (FileNotFoundException |CantCreateFileException e) {
            throw new CantGetIntraUserIdentityProfileImageException("CAN'T GET PRIVATE KEY ", e, "Error getting developer identity private keys file.", null);
        }

        return profileImage;
    }

    /**
     * <p>Method that check if alias exists.
     * @param alias
     * @return boolean exists
     * @throws CantCreateNewDeveloperException
     */
    private boolean aliasExists (String alias) throws CantCreateNewDeveloperException {


        DatabaseTable table;
        /**
         * Get developers identities list.
         * I select records on table
         */

        try {
            table = this.database.getTable (IntraUserIdentityDatabaseConstants.INTRA_USER_TABLE_NAME);

            if (table == null) {
                throw new CantGetUserDeveloperIdentitiesException("Cant check if alias exists, table not \" + DeveloperIdentityDatabaseConstants.DEVELOPER_TABLE_NAME + \" found.", "Plugin Identity", "Cant check if alias exists, table not \" + DeveloperIdentityDatabaseConstants.DEVELOPER_TABLE_NAME + \" found.");
            }

            table.setStringFilter(IntraUserIdentityDatabaseConstants.INTRA_USER_ALIAS_COLUMN_NAME, alias, DatabaseFilterType.EQUAL);
            table.loadToMemory();

            return table.getRecords ().size () > 0;


        } catch (CantLoadTableToMemoryException em) {
            throw new CantCreateNewDeveloperException (em.getMessage(), em, "Plugin Identity", "Cant load " + IntraUserIdentityDatabaseConstants.INTRA_USER_TABLE_NAME + " table in memory.");

        } catch (Exception e) {
            throw new CantCreateNewDeveloperException (e.getMessage(), e, "Plugin Identity", "Cant check if alias exists, unknown failure.");
        }
    }

}