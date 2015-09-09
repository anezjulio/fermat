# CBP Modules Specification

This is the simplified specification of the Crypto Broker Platform plugins

## Android

### Wallet
* [Crypto Broker](android/reference_wallet/fermat-android-reference-wallet-crypto-broker-bitdubai/): frontend de las actividades financieras de un Crypto Broker(vender crypto, vender cash, inyeccion de capital) y provee un balance contable unificado
* [Crypto Customer](android/reference_wallet/fermat-android-reference-wallet-crypto-customer-bitdubai/): frontend de las actividades financieras de un Crypto Customer(comprar crypto, comprar cash) y provee un balance contable unificado

### SubApp
* [Crypto Broker](android/sup_app/fermat-android-sub-app-crypto-broker-bitdubai/): frontend de la administracion de identidades de los Brokers, la relacion con otros brokers y descubrimiento de Brokers para los Customers (solicitudes de conexion, acuerdos especiales, etc)
* [Crypto Customers](android/sup_app/fermat-android-sub-app-crypto-customer-bitdubai/): frontend de la administracion de identidades de los Customers y la relacion con otros Customers (notificaciones de un Broker, acuerdos especiales, etc)
* [Customers](android/sup_app/fermat-android-sub-app-customers-bitdubai/): frontend de la gestion de contactos entre un Broker y sus Customers
* [SubApp Manager](android/sup_app/fermat-android-sub-app-sub-app-manager-bitdubai/): frontend del gestor de las SubApps instaladas en el dispositivo
* [Wallet Manager](android/sup_app/fermat-android-sub-app-wallet-manager/): frontend del gestor de las Wallets instaladas

## Java

### Wallet Module
* [Crypto Broker](plugin/wallet_module/fermat-cbp-plugin-wallet-module-crypto-broker-bitdubai/): gestion de la informacion y las actividades financieras de un Broker
* [Crypto Customer](plugin/wallet_module/fermat-cbp-plugin-wallet-module-crypto-customer-bitdubai/): gestion de la informacion y las actividades financieras de un Customer

### SubApp Module
* [Crypto Broker](plugin/sub_app_module/fermat-cbp-plugin-sub-app-module-crypto-broker-bitdubai/): administracion de identidades de los Brokers y la relacion con otros Brokers
* [Crypto Customer](plugin/sub_app_module/fermat-cbp-plugin-sub-app-module-crypto-customer-bitdubai/): administracion de identidades de los Customers y la relacion con otros Customers
* [Customers](plugin/sub_app_module/fermat-cbp-plugin-sub-app-module-customers-bitdubai/): gestion de contactos entre un Broker y sus Customers
* [SubApp Manager](plugin/sub_app_module/fermat-cbp-plugin-sub-app-module-sub-app-manager-bitdubai/): gestor de las SubApps instaladas en el dispositivo
* [Wallet Manager](plugin/sub_app_module/fermat-cbp-plugin-sub-app-module-wallet-manager-bitdubai/): gestor de las Wallets instaladas

### Contract
* [Crypto Broker Market Money Buy](plugin/contract/fermat-cbp-plugin-contract-crypto-broker-market-money-buy-bitdubai/): contrato de compra de Crypto de un Crypto Customer
* [Crypto Broker Fiat Money Buy](plugin/contract/fermat-cbp-plugin-contract-crypto-broker-fiat-money-buy-bitdubai/): contrato de compra de Fiat Cash de un Crypto Customer
* [Crypto Customer Market Money Sale](plugin/contract/fermat-cbp-plugin-contract-crypto-customer-market-money-sale-bitdubai/): contrato de venta de Crypto de un Crypto Customer
* [Crypto Customer Fiat Money Sale](plugin/contract/fermat-cbp-plugin-contract-crypto-customer-fiat-money-sale-bitdubai/): contrato de venta de Fiat Cash de un Crypto Customer

### Middleware
* [Customers](plugin/middleware/fermat-cbp-plugin-middleware-customers-bitdubai/): administra y subclasifica a los Crypto Customers de un Crypto Broker
* [Crypto Broker Identity Wallet Linker](plugin/middleware/fermat-cbp-plugin-middleware-crypto-broker-identity-wallet-linker-bitdubai/): relaciona una Crypto Broker Identity con una Crypto Broker Wallet. (no seria mejor llamar a esto Crypto Broker Wallet Settings?)

### Actor
* [Crypto Broker](plugin/actor/fermat-cbp-plugin-actor-crypto-broker-bitdubai/): administra la relacion con los Brokers (establecer conexion, listar contactos de este tipo, etc)
* [Crypto Customer](plugin/actor/fermat-cbp-plugin-actor-crypto-customer-bitdubai/): administra la relacion con los Customers

### Agent
* **Crypto Broker**: evaluacion de balances consolidados y calculo de indices para la ganancia en las Business Transactions de un Crypto Broker.

### World
* **Fiat Index**: establece la relacion de valor entre dos monedas Fiat (por ejemplo: bolivar vs dolar).

### Business Transaction
* **Crypto Broker Market Crypto Stock Replenish**: recarga de stock Crypto de Market Money.
* **Crypto Broker Fiat Cash Stock Replenish**: recarga de stock Cash de Fiat Money.
* **Crypto Broker Fiat Bank Stock Replenish**: recarga de stock Bank de Market Money.
* **Crypto Customer Market Crypto Sale**: venta de monedas Crypto del Broker a otros actores.
* **Crypto Customer Fiat Cash Sale**: venta de monedas Cash Fiat del Broker a otros actores.
* **Crypto Customer Fiat Bank Sale**: venta de monedas Cash Fiat del Broker a otros actores.
* **Crypto Broker Market Crypto Buy**: compra de monedas Crypto del Customer al Broker.
* **Crypto Broker Fiat Cash Buy**: compra de monedas Cash Fiat del Customer al Broker.
* **Crypto Broker Fiat Bank Buy**: compra de monedas Cash Fiat del Customer al Broker.

### Crypto Transaction (no esta cerrada la idea)(reutilizamos lo del CCP?)

* **Send Crypto**: envio de monedas Crypto a traves del Blockchain.
* **Receive Crypto**: recepcion de monedas Crypto a traves del Blockchain.

### Fiat Cash Transaction
* **Give Fiat Cash On Hand**: registro de pago con efectivo Fiat Cash.
* **Receive Fiat Cash On Hand**: registro de cobro con efectivo Fiat Cash.
* **Send Fiat Cash Delivery**: envio de Fiat Cash a traves de un tercero.
* **Receive Fiat Cash Delivery**: recepcion de Fiat Cash a traves de un tercero.

### Fiat Bank Transaction
* **Make Offline Fiat Bank Transfer**: registro manual de un deposito hecho con Fiat en una cuenta bancaria.
* **Receive Offline Fiat Bank Transfer**: registro manual de recepcion de un deposito hecho con Fiat en una cuenta bancaria.

### Wallet (reutilizamos las wallets crypto del CCP?)
* **Crypto Broker Market Crypto**: gestiona el balance crypto del Crypto Broker.
* **Crypto Broker Fiat Cash**: gestiona el balance efectivo del Crypto Broker.
* **Crypto Broker Fiat Bank**: gestiona el balance bancario del Crypto Broker (puede estar relacionado a multiples cuentas bancarias).
* **Crypto Customer Market Crypto**: gestiona el balance crypto del Crypto Customer.

### Request
* **Crypto Broker Buy**: gestiona la solicitud de compra del Crypto Customer al Crypto Broker

### Identity
* **Crypto Broker**: gestiona la Clave Privada y Publica del Broker asociadas con un alias.
* **Crypto Customer**: gestiona la Clave Privava y Publica del Customer asociadas con un alias.

### Network Service
* **Crypto Broker**: maneja la comunicacion entre Brokers y los actores que deseen comunicarse con el (incluido otros Brokers)
* **Crypto Customer**: maneja la comunicacion Customer - Broker.