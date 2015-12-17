package com.iobeam.spark.streams.examples.busbunching

/**
 * Temporary way to get route lengths until configs are deployed
 */
object RouteLengths {

    val ROUTE_MAP = Map("MTA NYCT_B1" -> 6166.06068082,
        "MTA NYCT_B11" -> 9727.50703026,
        "MTA NYCT_B12" -> 6583.88195276,
        "MTA NYCT_B13" -> 10540.4591788,
        "MTA NYCT_B14" -> 8520.98171847,
        "MTA NYCT_B15" -> 11991.1818631,
        "MTA NYCT_B16" -> 12538.2609832,
        "MTA NYCT_B17" -> 6887.02304061,
        "MTA NYCT_B2" -> 3999.85722802,
        "MTA NYCT_B20" -> 6879.05367712,
        "MTA NYCT_B24" -> 9940.44524968,
        "MTA NYCT_B25" -> 9280.1969867,
        "MTA NYCT_B26" -> 9659.96956915,
        "MTA NYCT_B3" -> 4856.01638957,
        "MTA NYCT_B31" -> 4558.89380012,
        "MTA NYCT_B32" -> 5792.8658185,
        "MTA NYCT_B35" -> 10043.8793657,
        "MTA NYCT_B36" -> 7716.45290791,
        "MTA NYCT_B37" -> 9818.4276234,
        "MTA NYCT_B38" -> 8373.02294415,
        "MTA NYCT_B39" -> 3017.13581679,
        "MTA NYCT_B4" -> 14399.5689847,
        "MTA NYCT_B41" -> 7372.26378296,
        "MTA NYCT_B42" -> 2067.49193497,
        "MTA NYCT_B43" -> 10547.0942905,
        "MTA NYCT_B44+" -> 13440.8364671,
        "MTA NYCT_B44" -> 15660.6913351,
        "MTA NYCT_B45" -> 7156.47148178,
        "MTA NYCT_B46" -> 8820.17013576,
        "MTA NYCT_B47" -> 12673.3585811,
        "MTA NYCT_B48" -> 9517.59791157,
        "MTA NYCT_B49" -> 4735.9751468,
        "MTA NYCT_B52" -> 8224.85780874,
        "MTA NYCT_B54" -> 6824.28776948,
        "MTA NYCT_B57" -> 12934.7463837,
        "MTA NYCT_B6" -> 18214.8213971,
        "MTA NYCT_B60" -> 12945.4868713,
        "MTA NYCT_B61" -> 9808.62607756,
        "MTA NYCT_B62" -> 11866.5112214,
        "MTA NYCT_B63" -> 2253.72169916,
        "MTA NYCT_B64" -> 10643.4620758,
        "MTA NYCT_B65" -> 6923.34231307,
        "MTA NYCT_B67" -> 11313.7996827,
        "MTA NYCT_B68" -> 12221.9582367,
        "MTA NYCT_B69" -> 2876.78325756,
        "MTA NYCT_B7" -> 10685.0994942,
        "MTA NYCT_B70" -> 7532.59487024,
        "MTA NYCT_B74" -> 1270.85696079,
        "MTA NYCT_B8" -> 14180.0590425,
        "MTA NYCT_B82" -> 15657.8417391,
        "MTA NYCT_B83" -> 5826.80615057,
        "MTA NYCT_B84" -> 3568.96889479,
        "MTA NYCT_B9" -> 10738.1656401,
        "MTA NYCT_BX1" -> 12073.3310655,
        "MTA NYCT_BX10" -> 11794.0843659,
        "MTA NYCT_BX11" -> 7839.76284701,
        "MTA NYCT_BX12+" -> 12836.0705331,
        "MTA NYCT_BX12" -> 3076.0567701,
        "MTA NYCT_BX13" -> 3186.1836537,
        "MTA NYCT_BX15" -> 5738.95784694,
        "MTA NYCT_BX16" -> 4166.6836015,
        "MTA NYCT_BX17" -> 9196.69150563,
        "MTA NYCT_BX18" -> 2268.80873361,
        "MTA NYCT_BX19" -> 10386.329682,
        "MTA NYCT_BX2" -> 11220.7308662,
        "MTA NYCT_BX21" -> 1332.64116343,
        "MTA NYCT_BX22" -> 10595.7400187,
        "MTA NYCT_BX24" -> 6778.0512048,
        "MTA NYCT_BX26" -> 2617.75863962,
        "MTA NYCT_BX27" -> 3788.29588897,
        "MTA NYCT_BX28" -> 10766.3527853,
        "MTA NYCT_BX29" -> 10094.1453768,
        "MTA NYCT_BX3" -> 5646.99384627,
        "MTA NYCT_BX30" -> 11683.5887865,
        "MTA NYCT_BX31" -> 6722.88478861,
        "MTA NYCT_BX32" -> 8620.59013422,
        "MTA NYCT_BX33" -> 4396.31675992,
        "MTA NYCT_BX34" -> 6268.55773317,
        "MTA NYCT_BX35" -> 6163.30917304,
        "MTA NYCT_BX36" -> 11849.9392811,
        "MTA NYCT_BX38" -> 9847.17212184,
        "MTA NYCT_BX39" -> 11349.2090889,
        "MTA NYCT_BX4" -> 7573.17196635,
        "MTA NYCT_BX40" -> 7658.35342575,
        "MTA NYCT_BX41+" -> 8490.70856524,
        "MTA NYCT_BX41" -> 8489.11033473,
        "MTA NYCT_BX42" -> 5878.8058484,
        "MTA NYCT_BX46" -> 3126.96155279,
        "MTA NYCT_BX4A" -> 7956.81643001,
        "MTA NYCT_BX5" -> 4105.00898774,
        "MTA NYCT_BX6" -> 4746.90840001,
        "MTA NYCT_BX7" -> 9763.70378773,
        "MTA NYCT_BX8" -> 6163.52703561,
        "MTA NYCT_BX9" -> 9926.99968462,
        "MTA NYCT_M1" -> 12365.6165961,
        "MTA NYCT_M10" -> 8524.32707891,
        "MTA NYCT_M100" -> 12217.4153724,
        "MTA NYCT_M101" -> 7616.20854624,
        "MTA NYCT_M102" -> 12132.8055733,
        "MTA NYCT_M103" -> 12278.3014442,
        "MTA NYCT_M104" -> 8805.34604786,
        "MTA NYCT_M106" -> 4002.18686405,
        "MTA NYCT_M11" -> 12173.9256292,
        "MTA NYCT_M116" -> 3959.98561736,
        "MTA NYCT_M12" -> 5545.0803905,
        "MTA NYCT_M14A" -> 6184.18945789,
        "MTA NYCT_M14D" -> 2981.55734272,
        "MTA NYCT_M15+" -> 13913.7415869,
        "MTA NYCT_M15" -> 11900.4724008,
        "MTA NYCT_M2" -> 15178.0937515,
        "MTA NYCT_M20" -> 9937.37678717,
        "MTA NYCT_M21" -> 4179.19214255,
        "MTA NYCT_M22" -> 4681.07079683,
        "MTA NYCT_M23" -> 3942.52578894,
        "MTA NYCT_M3" -> 17036.6880028,
        "MTA NYCT_M31" -> 6095.4228006,
        "MTA NYCT_M34+" -> 3657.09193571,
        "MTA NYCT_M34A+" -> 3570.17412564,
        "MTA NYCT_M35" -> 5678.41264927,
        "MTA NYCT_M4" -> 15468.7342639,
        "MTA NYCT_M42" -> 3274.00976653,
        "MTA NYCT_M5" -> 19711.9652163,
        "MTA NYCT_M50" -> 3461.67698916,
        "MTA NYCT_M57" -> 2306.38687237,
        "MTA NYCT_M60+" -> 14453.1536629,
        "MTA NYCT_M66" -> 3004.26682466,
        "MTA NYCT_M7" -> 12196.7267437,
        "MTA NYCT_M72" -> 4955.10157159,
        "MTA NYCT_M79" -> 3530.91467381,
        "MTA NYCT_M8" -> 3274.35146616,
        "MTA NYCT_M86+" -> 3427.87938822,
        "MTA NYCT_M9" -> 8188.31302837,
        "MTA NYCT_M96" -> 3080.98834073,
        "MTA NYCT_M98" -> 11567.9427125,
        "MTA NYCT_Q1" -> 5806.82495769,
        "MTA NYCT_Q12" -> 9525.898613,
        "MTA NYCT_Q13" -> 8399.86293385,
        "MTA NYCT_Q15" -> 4027.28122951,
        "MTA NYCT_Q15A" -> 6943.09712806,
        "MTA NYCT_Q16" -> 7970.1538335,
        "MTA NYCT_Q17" -> 11411.0121104,
        "MTA NYCT_Q2" -> 5468.13862215,
        "MTA NYCT_Q20A" -> 6787.54191618,
        "MTA NYCT_Q20B" -> 15053.3956914,
        "MTA NYCT_Q24" -> 14021.0006326,
        "MTA NYCT_Q27" -> 5916.35882984,
        "MTA NYCT_Q28" -> 6998.27896849,
        "MTA NYCT_Q3" -> 7036.01019319,
        "MTA NYCT_Q30" -> 5888.03456389,
        "MTA NYCT_Q31" -> 12123.6392046,
        "MTA NYCT_Q32" -> 12070.4834689,
        "MTA NYCT_Q36" -> 9004.08837976,
        "MTA NYCT_Q4" -> 6046.37366761,
        "MTA NYCT_Q42" -> 3739.60238672,
        "MTA NYCT_Q43" -> 10883.1658456,
        "MTA NYCT_Q44" -> 13036.4687853,
        "MTA NYCT_Q46" -> 11876.7055805,
        "MTA NYCT_Q48" -> 7445.56504177,
        "MTA NYCT_Q5" -> 2156.26592275,
        "MTA NYCT_Q54" -> 5239.29477359,
        "MTA NYCT_Q55" -> 3102.92395885,
        "MTA NYCT_Q56" -> 10555.4146866,
        "MTA NYCT_Q58" -> 7946.37323019,
        "MTA NYCT_Q59" -> 11630.5782415,
        "MTA NYCT_Q76" -> 15285.750449,
        "MTA NYCT_Q77" -> 10735.2303202,
        "MTA NYCT_Q83" -> 6789.54346825,
        "MTA NYCT_Q84" -> 8463.02321796,
        "MTA NYCT_Q85" -> 10965.6606446,
        "MTA NYCT_Q88" -> 8644.91037136,
        "MTA NYCT_S40" -> 9495.80595129,
        "MTA NYCT_S42" -> 4309.01321985,
        "MTA NYCT_S44" -> 6937.54327801,
        "MTA NYCT_S46" -> 9141.90917971,
        "MTA NYCT_S48" -> 10599.7331813,
        "MTA NYCT_S51" -> 12416.9130232,
        "MTA NYCT_S52" -> 4812.47470219,
        "MTA NYCT_S53" -> 2783.06374192,
        "MTA NYCT_S54" -> 9957.38653644,
        "MTA NYCT_S55" -> 6241.58278655,
        "MTA NYCT_S56" -> 3860.51884128,
        "MTA NYCT_S57" -> 7546.89426555,
        "MTA NYCT_S59" -> 10603.8840165,
        "MTA NYCT_S61" -> 7401.08870389,
        "MTA NYCT_S62" -> 6505.02458213,
        "MTA NYCT_S66" -> 12496.0844345,
        "MTA NYCT_S74" -> 14910.668747,
        "MTA NYCT_S76" -> 4754.09629132,
        "MTA NYCT_S78" -> 31260.6205857,
        "MTA NYCT_S79+" -> 7177.17352087,
        "MTA NYCT_S93" -> 13913.0974423,
        "MTA NYCT_X1" -> 39950.7049328,
        "MTA NYCT_X10" -> 37497.3836113,
        "MTA NYCT_X10B" -> 38361.0925205,
        "MTA NYCT_X12" -> 33052.0896766,
        "MTA NYCT_X14" -> 38605.8824884,
        "MTA NYCT_X15" -> 33235.5950877,
        "MTA NYCT_X17" -> 38560.1565487,
        "MTA NYCT_X17A" -> 33973.8466693,
        "MTA NYCT_X17J" -> 45793.0420801,
        "MTA NYCT_X19" -> 41217.4519061,
        "MTA NYCT_X2" -> 33490.062266,
        "MTA NYCT_X22" -> 53150.9601517,
        "MTA NYCT_X27" -> 22752.753508,
        "MTA NYCT_X28" -> 24743.3535491,
        "MTA NYCT_X30" -> 46739.6579042,
        "MTA NYCT_X31" -> 49444.5299296,
        "MTA NYCT_X37" -> 25675.655341,
        "MTA NYCT_X4" -> 34687.7657423,
        "MTA NYCT_X42" -> 40341.8835449,
        "MTA NYCT_X5" -> 42724.2156398,
        "MTA NYCT_X63" -> 36127.7366266,
        "MTA NYCT_X64" -> 32765.2979182,
        "MTA NYCT_X68" -> 32817.3858619,
        "MTA NYCT_X7" -> 41125.7582753,
        "MTA NYCT_X8" -> 34900.743865,
        "MTA NYCT_X9" -> 32711.5129027,
        "MTABC_B100" -> 1030.82791211,
        "MTABC_B103" -> 7334.120417625,
        "MTABC_BM1" -> 23815.4419245,
        "MTABC_BM2" -> 33338.5649645,
        "MTABC_BM3" -> 31775.5968231,
        "MTABC_BM4" -> 22135.0048321,
        "MTABC_BM5" -> 27509.1562099,
        "MTABC_BX23" -> 2902.22367754,
        "MTABC_BXM1" -> 22927.4629151,
        "MTABC_BXM10" -> 22807.2054203,
        "MTABC_BXM11" -> 24680.5865147,
        "MTABC_BXM18" -> 28186.9689111,
        "MTABC_BXM2" -> 24458.2645346,
        "MTABC_BXM3" -> 24100.8605849,
        "MTABC_BXM4" -> 22580.1339246,
        "MTABC_BXM6" -> 18672.3207286,
        "MTABC_BXM7" -> 2134.58000039,
        "MTABC_BXM8" -> 29165.1517955,
        "MTABC_BXM9" -> 2613.32109999,
        "MTABC_Q06" -> 8738.69002459,
        "MTABC_Q07" -> 4940.74220223,
        "MTABC_Q08" -> 12701.6066957,
        "MTABC_Q09" -> 5276.81966247,
        "MTABC_Q10" -> 7260.22956318,
        "MTABC_Q100" -> 7498.34120475,
        "MTABC_Q101" -> 8044.05961371,
        "MTABC_Q102" -> 7902.93496739,
        "MTABC_Q103" -> 5544.4238476,
        "MTABC_Q104" -> 4175.53858839,
        "MTABC_Q11" -> 7520.99095314,
        "MTABC_Q110" -> 7544.94606155,
        "MTABC_Q111" -> 9918.67548295,
        "MTABC_Q112" -> 6332.58523261,
        "MTABC_Q113" -> 16291.8027236,
        "MTABC_Q114" -> 18636.3290843,
        "MTABC_Q18" -> 962.315304628,
        "MTABC_Q19" -> 10258.5920719,
        "MTABC_Q21" -> 13394.4422162,
        "MTABC_Q22" -> 13351.8848227,
        "MTABC_Q23" -> 11088.6690086,
        "MTABC_Q25" -> 13564.6007375,
        "MTABC_Q29" -> 6392.67835097,
        "MTABC_Q33" -> 3997.83480357,
        "MTABC_Q34" -> 11368.9473757,
        "MTABC_Q35" -> 4890.35205891,
        "MTABC_Q37" -> 8123.87377897,
        "MTABC_Q38" -> 12762.5344638,
        "MTABC_Q39" -> 11473.0983841,
        "MTABC_Q40" -> 4852.7169433,
        "MTABC_Q41" -> 11885.9742445,
        "MTABC_Q47" -> 10173.7337722,
        "MTABC_Q49" -> 4026.4638595,
        "MTABC_Q50" -> 19051.3221887,
        "MTABC_Q52" -> 19997.2504539,
        "MTABC_Q53" -> 22978.4875035,
        "MTABC_Q60" -> 15603.8054888,
        "MTABC_Q64" -> 3833.55152311,
        "MTABC_Q65" -> 4576.04647844,
        "MTABC_Q66" -> 7433.88173366,
        "MTABC_Q67" -> 11065.3354494,
        "MTABC_Q69" -> 7758.57284116,
        "MTABC_Q72" -> 5600.07764633,
        "MTABC_QM1" -> 23858.3501023,
        "MTABC_QM10" -> 15128.5497106,
        "MTABC_QM11" -> 26416.462598,
        "MTABC_QM12" -> 18170.8875244,
        "MTABC_QM15" -> 25297.9469916,
        "MTABC_QM16" -> 36768.6810014,
        "MTABC_QM17" -> 38342.1630665,
        "MTABC_QM18" -> 25255.7251824,
        "MTABC_QM2" -> 27397.1493208,
        "MTABC_QM20" -> 27589.0835849,
        "MTABC_QM21" -> 29340.2209104,
        "MTABC_QM24" -> 23041.7548136,
        "MTABC_QM25" -> 29323.7168966,
        "MTABC_QM3" -> 30419.7575308,
        "MTABC_QM4" -> 18599.6163791,
        "MTABC_QM5" -> 11141.6305958,
        "MTABC_QM6" -> 31767.4742266,
        "MTABC_QM7" -> 30213.1316514,
        "MTABC_QM8" -> 39321.8808204)


    def getRouteLength(route: String, direction: Int): Double = {
        ROUTE_MAP.getOrElse(route, 0.0)
    }
}
