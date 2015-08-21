package com.bitdubai.fermat_api.layer.dmp_middleware.wallet_factory.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.Languages;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.dmp_identity.translator.interfaces.Translator;

import java.util.UUID;

/**
 * Created by eze on 2015.07.15..
 */
public interface LanguageDescriptorFactoryProject extends DescriptorFactoryProject{

    Languages getLanguagesName();
    String getLanguageLabel();
    Translator getTranslator();
}
