package com.indepthkenya.mpesa;

import com.indepthkenya.mpesa.utils.ApiCreationUtil;

/**
 * Hello world!
 *
 */
public class App
{
    public App(){
        //start apis
        ApiCreationUtil.startAPIs(9989);
    }
    public static void main( String[] args )
    {
        try {
            App app = new App();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
