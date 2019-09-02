module soderstrand.martin {

    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.beans;
    requires spring.core;

    opens soderstrand.martin.filereader;
    opens soderstrand.martin.filereader.util;
}