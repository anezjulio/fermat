package com.bitdubai.fermat_dap_plugin.layer.middleware.asset.issuer.developer.bitdubai.version_1.structure.database;

import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.resources_structure.Resource;
import com.bitdubai.fermat_api.layer.all_definition.resources_structure.enums.ResourceDensity;
import com.bitdubai.fermat_api.layer.all_definition.resources_structure.enums.ResourceType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_dap_api.layer.all_definition.contracts.ContractProperty;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAssetContract;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.State;
import com.bitdubai.fermat_dap_api.layer.dap_identity.asset_issuer.interfaces.IdentityAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_middleware.dap_asset_factory.enums.AssetBehavior;
import com.bitdubai.fermat_dap_api.layer.dap_middleware.dap_asset_factory.exceptions.CantDeleteAsserFactoryException;
import com.bitdubai.fermat_dap_api.layer.dap_middleware.dap_asset_factory.interfaces.AssetFactory;
import com.bitdubai.fermat_dap_plugin.layer.middleware.asset.issuer.developer.bitdubai.version_1.exceptions.DatabaseOperationException;
import com.bitdubai.fermat_dap_plugin.layer.middleware.asset.issuer.developer.bitdubai.version_1.exceptions.MissingAssetDataException;
import com.bitdubai.fermat_dap_plugin.layer.middleware.asset.issuer.developer.bitdubai.version_1.structure.AssetIssuerIdentity;
import com.bitdubai.fermat_dap_plugin.layer.middleware.asset.issuer.developer.bitdubai.version_1.structure.functional.AssetFactoryRecord;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by franklin on 15/09/15.
 */
public class AssetFactoryMiddlewareDao {

    Database database;
    UUID pluginId;
    /**
     * DealsWithPluginDatabaseSystem interface variable and implementation
     */
    PluginDatabaseSystem pluginDatabaseSystem;

    /**
     * Constructor
     */
    public AssetFactoryMiddlewareDao(PluginDatabaseSystem pluginDatabaseSystem, UUID pluginId) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginId = pluginId;
    }

    private DatabaseTable getDatabaseTable(String tableName) {

        return database.getTable(tableName);
    }

    private Database openDatabase() throws CantOpenDatabaseException, CantCreateDatabaseException {
        try {
            database = pluginDatabaseSystem.openDatabase(this.pluginId, AssertFactoryMiddlewareDatabaseConstant.DATABASE_NAME);

        } catch (DatabaseNotFoundException e) {
            AssetFactoryMiddlewareDatabaseFactory assetFactoryMiddlewareDatabaseFactory = new AssetFactoryMiddlewareDatabaseFactory(this.pluginDatabaseSystem);
            database = assetFactoryMiddlewareDatabaseFactory.createDatabase(this.pluginId, AssertFactoryMiddlewareDatabaseConstant.DATABASE_NAME);
        }
        return database;
    }

    private DatabaseTableRecord getAssetFactoryProjectRecord(AssetFactory assetFactory) throws DatabaseOperationException {
        DatabaseTable databaseTable = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ID_COLUMN, assetFactory.getFactoryId());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ASSET_PUBLIC_KEY_COLUMN, assetFactory.getAssetPublicKey());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_NAME_COLUMN, assetFactory.getName());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_DESCRIPTION_COLUMN, assetFactory.getDescription());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ISSUER_IDENTITY_ALIAS_COLUMN, assetFactory.getIdentyAssetIssuer().getAlias());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ISSUER_IDENTITY_SIGNATURE_COLUMN, "signature");
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ISSUER_IDENTITY_PUBLIC_KEY_COLUMN, assetFactory.getIdentyAssetIssuer().getPublicKey());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_STATE_COLUMN, assetFactory.getState().getCode());
        record.setLongValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_FEE_COLUMN, assetFactory.getFee());
        record.setLongValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_AMOUNT_COLUMN, assetFactory.getAmount());
        record.setLongValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_QUANTITY_COLUMN, assetFactory.getQuantity());
        record.setLongValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_TOTAL_QUANTITY_COLUMN, assetFactory.getTotalQuantity());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IS_REDEEMABLE, String.valueOf(assetFactory.getIsRedeemable()));
        if (assetFactory.getExpirationDate() != null) {
            record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_EXPIRATION_DATE, assetFactory.getExpirationDate().toString());
        } else
            record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_EXPIRATION_DATE, null);

        if (assetFactory.getCreationTimestamp() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            calendar.setTime(new Date());
            assetFactory.setCreationTimestamp(new Timestamp(calendar.getTime().getTime()));
        }
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CREATION_TIME_COLUMN, assetFactory.getCreationTimestamp().toString());
        if (assetFactory.getLastModificationTimestamp() == null) {
            Date date = new Date();
            assetFactory.setLastModificationTimeststamp(new Timestamp(date.getTime()));
        }
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_LAST_UPDATE_TIME_COLUMN, assetFactory.getLastModificationTimestamp().toString());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ASSET_BEHAVIOR_COLUMN, assetFactory.getAssetBehavior().getCode());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ASSET_WALLET_PUBLIC_KEY, assetFactory.getWalletPublicKey());

        return record;
    }

    private DatabaseTableRecord getResourceDataRecord(String assetPublicKey, Resource resource) throws DatabaseOperationException, MissingAssetDataException {
        DatabaseTable databaseTable = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setUUIDValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_ID_COLUMN, resource.getId());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_NAME_COLUMN, resource.getName());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_ASSET_PUBLIC_KEY_COLUMN, assetPublicKey);
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_FILE_NAME_COLUMN, resource.getFileName());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_RESOURCE_DENSITY_COLUMN, resource.getResourceDensity().getCode());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_RESOURCE_TYPE_COLUMN, resource.getResourceType().value());
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_PATH_COLUMN, "aca va el path del archivo");

        return record;
    }

    private DatabaseTableRecord getContractDataRecord(String assetPublicKey,
                                                      String name,
                                                      String value) throws DatabaseOperationException, MissingAssetDataException {
        DatabaseTable databaseTable = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_ASSET_PUBLIC_KEY_COLUMN, assetPublicKey);
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_NAME_COLUMN, name);
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_VALUE_COLUMN, value);

        return record;
    }

    private DatabaseTableRecord getIdentityIssuerDataRecord(String assetPublicKey,
                                                            String publicKey,
                                                            String name,
                                                            String signature) throws DatabaseOperationException, MissingAssetDataException {
        DatabaseTable databaseTable = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_TABLE_NAME);
        DatabaseTableRecord record = databaseTable.getEmptyRecord();

        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_PUBLIC_KEY_COLUMN, publicKey);
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_NAME_COLUMN, name);
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_ASSET_PUBLIC_KEY_COLUMN, assetPublicKey);
        record.setStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_SIGNATURE_COLUMN, signature);

        return record;
    }

    private DatabaseTransaction addResourceRecordsToTransaction(DatabaseTransaction transaction, AssetFactory assetFactory) throws DatabaseOperationException, MissingAssetDataException, CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_TABLE_NAME);

        if (assetFactory.getResources() != null) {
            for (Resource resources : assetFactory.getResources()) {
                DatabaseTableRecord record = getResourceDataRecord(assetFactory.getAssetPublicKey(), resources);
                DatabaseTableFilter filter = getResourceFilter(resources.getId().toString());
                filter.setValue(resources.getId().toString());
                if (isNewRecord(table, filter))
                    //New Records
                    transaction.addRecordToInsert(table, record);
                else {
                    //update Records
                    table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                    transaction.addRecordToUpdate(table, record);
                }
            }
        }
        return transaction;
    }

    private DatabaseTransaction addContractRecordsToTransaction(DatabaseTransaction transaction, AssetFactory assetFactory) throws DatabaseOperationException, MissingAssetDataException, CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_TABLE_NAME);

        if (assetFactory.getContractProperties() != null) {
            for (ContractProperty contractProperties : assetFactory.getContractProperties()) {
                DatabaseTableRecord record = getContractDataRecord(assetFactory.getAssetPublicKey(), contractProperties.getName(),
                        contractProperties.getValue() != null ? contractProperties.getValue().toString() : null);
                DatabaseTableFilter filter = getContractFilter(contractProperties.getName());
//                filter.setValue(contractProperties.getName());
                if (isNewRecord(table, filter))
                    //New Records
                    transaction.addRecordToInsert(table, record);
                else {
                    //update Records
                    table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                    transaction.addRecordToUpdate(table, record);
                }
            }
        }

        return transaction;
    }

    private DatabaseTransaction addIdentityIssuerRecordsToTransaction(DatabaseTransaction transaction, AssetFactory assetFactory) throws DatabaseOperationException, MissingAssetDataException, CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_TABLE_NAME);

        DatabaseTableRecord record = getIdentityIssuerDataRecord(assetFactory.getAssetPublicKey(), assetFactory.getIdentyAssetIssuer().getPublicKey(), assetFactory.getIdentyAssetIssuer().getAlias(), "signature");
        DatabaseTableFilter filter = getIdentityAssetPublicKeyFilter(assetFactory.getAssetPublicKey());
        if (isNewRecord(table, filter))
            //New Records
            transaction.addRecordToInsert(table, record);
        else {
            table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
            transaction.addRecordToUpdate(table, record);
        }

        return transaction;
    }

    private DatabaseTableFilter getIdentityAssetPublicKeyFilter(String value) {
        DatabaseTableFilter filter = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_TABLE_NAME).getEmptyTableFilter();
        filter.setColumn(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_ASSET_PUBLIC_KEY_COLUMN);
        filter.setType(DatabaseFilterType.EQUAL);
        filter.setValue(value);

        return filter;
    }

    private DatabaseTableFilter getContractFilter(String value) {
        DatabaseTableFilter filter = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_TABLE_NAME).getEmptyTableFilter();
        filter.setColumn(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_NAME_COLUMN);
        filter.setType(DatabaseFilterType.EQUAL);
        filter.setValue(value);

        return filter;
    }

    private DatabaseTableFilter getResourceFilter(String value) {
        DatabaseTableFilter filter = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_TABLE_NAME).getEmptyTableFilter();
        filter.setColumn(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_ID_COLUMN);
        filter.setType(DatabaseFilterType.EQUAL);
        filter.setValue(value);

        return filter;
    }

    private boolean isNewRecord(DatabaseTable table, DatabaseTableFilter filter) throws CantLoadTableToMemoryException {
        table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
        table.loadToMemory();
        return table.getRecords().isEmpty();
    }

    private List<DatabaseTableRecord> getResourcesData(String assetFactoryPublicKey) throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_TABLE_NAME);

        table.addStringFilter(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_ASSET_PUBLIC_KEY_COLUMN, assetFactoryPublicKey, DatabaseFilterType.EQUAL);
        table.loadToMemory();

        return table.getRecords();
    }

    private List<DatabaseTableRecord> getIdentityIssuerData(String assetFactoryPublicKey) throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_TABLE_NAME);

        table.addStringFilter(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_ASSET_PUBLIC_KEY_COLUMN, assetFactoryPublicKey, DatabaseFilterType.EQUAL);
        table.loadToMemory();

        return table.getRecords();
    }

    private List<DatabaseTableRecord> getContractsData(String assetFactoryPublicKey) throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_TABLE_NAME);

        table.addStringFilter(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_ASSET_PUBLIC_KEY_COLUMN, assetFactoryPublicKey, DatabaseFilterType.EQUAL);
        table.loadToMemory();

        return table.getRecords();
    }

    private List<DatabaseTableRecord> getAssetFactoryData(DatabaseTableFilter... filters) throws CantLoadTableToMemoryException {
        DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_TABLE_NAME);
        for (DatabaseTableFilter filter : filters) {
            if (filter != null)
                table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
        }

        table.loadToMemory();

        return table.getRecords();
    }

    private AssetFactory getEmptyAssetFactory() {
        return new AssetFactoryRecord();
    }

    private AssetFactory getAssetFactory(final DatabaseTableRecord assetFactoriesRecord) throws CantLoadTableToMemoryException, DatabaseOperationException, InvalidParameterException {
        AssetFactory assetFactory = getEmptyAssetFactory();
        assetFactory.setFactoryId(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ID_COLUMN));
        assetFactory.setPublicKey(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ASSET_PUBLIC_KEY_COLUMN));
        assetFactory.setName(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_NAME_COLUMN));
        assetFactory.setDescription(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_DESCRIPTION_COLUMN));
        assetFactory.setAmount(assetFactoriesRecord.getLongValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_AMOUNT_COLUMN));
        assetFactory.setWalletPublicKey(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ASSET_WALLET_PUBLIC_KEY);
        AssetIssuerIdentity assetIssuerIdentity = new AssetIssuerIdentity();
        assetIssuerIdentity.setAlias(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ISSUER_IDENTITY_ALIAS_COLUMN));
        assetIssuerIdentity.setPublicKey(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_PUBLIC_KEY_COLUMN));
        assetFactory.setIdentityAssetIssuer(assetIssuerIdentity);
        assetFactory.setState(State.getByCode(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_STATE_COLUMN)));
        assetFactory.setFee(assetFactoriesRecord.getLongValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_FEE_COLUMN));
        assetFactory.setQuantity(assetFactoriesRecord.getIntegerValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_QUANTITY_COLUMN));
        assetFactory.setTotalQuantity(assetFactoriesRecord.getIntegerValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_TOTAL_QUANTITY_COLUMN));
        assetFactory.setCreationTimestamp(Timestamp.valueOf(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CREATION_TIME_COLUMN)));
        assetFactory.setLastModificationTimeststamp(Timestamp.valueOf(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_LAST_UPDATE_TIME_COLUMN)));
        assetFactory.setAssetBehavior(AssetBehavior.getByCode(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ASSET_BEHAVIOR_COLUMN)));
        //Object value = assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_EXPIRATION_DATE);

        String value = assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_EXPIRATION_DATE) != null ?
                !assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_EXPIRATION_DATE).equalsIgnoreCase("null") ? assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_EXPIRATION_DATE) : null : null;
        assetFactory.setExpirationDate(value != null ? Timestamp.valueOf(value) : null);


        assetFactory.setIsRedeemable(Boolean.valueOf(assetFactoriesRecord.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IS_REDEEMABLE)));

        return assetFactory;
    }

    public void removeAssetFactory(AssetFactory assetFactory) throws CantDeleteAsserFactoryException {
        try {
            database = openDatabase();

            DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_TABLE_NAME);
            DatabaseTable tableContracts = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_CONTRACT_TABLE_NAME);
            DatabaseTable tableResources = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_TABLE_NAME);
            DatabaseTable tableIdentityUser = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_TABLE_NAME);
            DatabaseTableRecord databaseTablerecord = getAssetFactoryProjectRecord(assetFactory);
            table.addStringFilter(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ASSET_PUBLIC_KEY_COLUMN, assetFactory.getAssetPublicKey(), DatabaseFilterType.EQUAL);
            table.loadToMemory();

            if (assetFactory.getResources() != null) {
                for (Resource resources : assetFactory.getResources()) {
                    DatabaseTableRecord record = getResourceDataRecord(assetFactory.getAssetPublicKey(), resources);
                    tableResources.deleteRecord(record);
                }
            }

            if (assetFactory.getContractProperties() != null) {
                for (ContractProperty contractProperties : assetFactory.getContractProperties()) {
                    DatabaseTableRecord record = getContractDataRecord(assetFactory.getAssetPublicKey(), contractProperties.getName(), contractProperties.getValue().toString());
                    tableContracts.deleteRecord(record);
                }
            }

            DatabaseTableRecord record = getIdentityIssuerDataRecord(assetFactory.getAssetPublicKey(), assetFactory.getIdentyAssetIssuer().getPublicKey(), assetFactory.getIdentyAssetIssuer().getAlias(), "signature");
            tableIdentityUser.deleteRecord(record);

            table.deleteRecord(databaseTablerecord);


        } catch (Exception exception) {
            throw new CantDeleteAsserFactoryException(exception, "Error delete Asset Factory", "Asset Factory - Delete");
        }
    }


    public void saveAssetFactoryData(AssetFactory assetFactory) throws DatabaseOperationException, MissingAssetDataException {


        try {
            database = openDatabase();
            DatabaseTransaction transaction = database.newTransaction();

            DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_TABLE_NAME);
            DatabaseTableRecord assetFactoryRecord = getAssetFactoryProjectRecord(assetFactory);
            DatabaseTableFilter filter = table.getEmptyTableFilter();
            filter.setType(DatabaseFilterType.EQUAL);
            filter.setValue(assetFactory.getAssetPublicKey());
            filter.setColumn(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ID_COLUMN);

            if (isNewRecord(table, filter))
                transaction.addRecordToInsert(table, assetFactoryRecord);
            else {
                table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                transaction.addRecordToUpdate(table, assetFactoryRecord);
            }

            // I wil add the Contracts to the transaction if there are any
            if (assetFactory.getContractProperties() != null)
                transaction = addContractRecordsToTransaction(transaction, assetFactory);
            // I wil add the resources to the transaction if there are any
            if (assetFactory.getResources() != null)
                transaction = addResourceRecordsToTransaction(transaction, assetFactory);
            // I wil add the identity issuer to the transaction if there are any
            if (assetFactory.getIdentyAssetIssuer() != null)
                transaction = addIdentityIssuerRecordsToTransaction(transaction, assetFactory);

            //I execute the transaction and persist the database side of the asset.
            database.executeTransaction(transaction);

        } catch (Exception e) {
            if (database != null)

                throw new DatabaseOperationException(DatabaseOperationException.DEFAULT_MESSAGE, e, "Error trying to save the Asset Factory in the database.", null);
        }
    }

    public void markAssetFactoryData(AssetFactory assetFactory) throws DatabaseOperationException, MissingAssetDataException {
        try {
            database = openDatabase();
            DatabaseTransaction transaction = database.newTransaction();

            DatabaseTable table = getDatabaseTable(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_TABLE_NAME);
            DatabaseTableRecord assetFactoryRecord = getAssetFactoryProjectRecord(assetFactory);
            DatabaseTableFilter filter = table.getEmptyTableFilter();
            filter.setType(DatabaseFilterType.EQUAL);
            filter.setValue(assetFactory.getFactoryId());
            filter.setColumn(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_ID_COLUMN);

            if (isNewRecord(table, filter))
                transaction.addRecordToInsert(table, assetFactoryRecord);
            else {
                table.addStringFilter(filter.getColumn(), filter.getValue(), filter.getType());
                transaction.addRecordToUpdate(table, assetFactoryRecord);
            }
            //I execute the transaction and persist the database side of the asset.
            database.executeTransaction(transaction);
        } catch (Exception e) {
            throw new DatabaseOperationException(DatabaseOperationException.DEFAULT_MESSAGE, e, "Error trying to save the Asset Factory in the database.", null);
        }
    }

    public List<AssetFactory> getAssetFactoryList(DatabaseTableFilter ... filters) throws DatabaseOperationException, InvalidParameterException, CantCreateFileException {
        try {
            openDatabase();
            List<AssetFactory> assetFactoryList = new ArrayList<>();

            // I will add the Asset Factory information from the database
            for (DatabaseTableRecord assetFactoriesRecord : getAssetFactoryData(filters)) {

                final AssetFactory assetFactory = getAssetFactory(assetFactoriesRecord);
                AssetIssuerIdentity assetIssuerIdentity = new AssetIssuerIdentity();
                // I will add the indetity issuer information from database
                for (final DatabaseTableRecord identityIssuerRecords : getIdentityIssuerData(assetFactory.getAssetPublicKey())) {
                    assetIssuerIdentity.setPublicKey(identityIssuerRecords.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_PUBLIC_KEY_COLUMN));
                    assetIssuerIdentity.setAlias(identityIssuerRecords.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_IDENTITY_ISSUER_NAME_COLUMN));
                }

                List<Resource> resources = new ArrayList<>();
                // I will add the resource properties information from database
                for (DatabaseTableRecord resourceRecords : getResourcesData(assetFactory.getAssetPublicKey())) {

                    Resource resource = new Resource();

                    resource.setId(resourceRecords.getUUIDValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_ID_COLUMN));
                    resource.setName(resourceRecords.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_NAME_COLUMN));
                    resource.setFileName(resourceRecords.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_FILE_NAME_COLUMN));
                    resource.setName(resourceRecords.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_NAME_COLUMN));
                    try {
                        resource.setResourceDensity(ResourceDensity.getByCode(resourceRecords.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_RESOURCE_DENSITY_COLUMN)));
                    } catch (InvalidParameterException e) {
                        resource.setResourceDensity(ResourceDensity.HDPI);
                    }
                    try {
                        resource.setResourceType(ResourceType.getByCode(resourceRecords.getStringValue(AssertFactoryMiddlewareDatabaseConstant.ASSET_FACTORY_RESOURCE_RESOURCE_TYPE_COLUMN)));
                    } catch (InvalidParameterException e) {
                        resource.setResourceType(ResourceType.IMAGE);
                    }
                    resource.setResourceBinayData(new byte[]{0xa, 0x2, 0xf, (byte) 0xff, (byte) 0xff, (byte) 0xff});
                    resources.add(resource);
                }

                //assetFactory.setContractProperties(contractProperties);
                assetFactory.setResources(resources);
                assetFactory.setIdentityAssetIssuer(assetIssuerIdentity);

                assetFactoryList.add(assetFactory);
            }


            return assetFactoryList;
        } catch (Exception e) {
            throw new DatabaseOperationException(DatabaseOperationException.DEFAULT_MESSAGE, e, "error trying to get assets factory from the database with filter: " + filters.toString(), null);
        }

    }
}
