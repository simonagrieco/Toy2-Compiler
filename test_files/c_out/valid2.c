#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <unistd.h>
#include <stdbool.h>
#define MAXCHAR 512

char* integer_to_str(int i){
int length= snprintf(NULL,0,"%d",i);
char* result= (char*) malloc(length+1);
snprintf(result,length+1,"%d",i);
return result;}

char* real_to_str(float i){
int length= snprintf(NULL,0,"%f",i);
char* result= (char*) malloc(length+1);
snprintf(result,length+1,"%f",i);
return result;}

char* char_to_str(char i){
int length= snprintf(NULL,0,"%c",i);
char* result= (char*) malloc(length+1);
snprintf(result,length+1,"%c",i);
return result;}

char* bool_to_str(bool i){
int length= snprintf(NULL,0,"%d",i);
char* result= (char*) malloc(length+1);
snprintf(result,length+1,"%d",i);
return result;}

char* str_concat(char* str1, char* str2){
char* result= (char*) malloc(sizeof(char)* MAXCHAR);
result=strcat(result,str1);
result=strcat(result,str2);
return result;}

char* read_str(){
char* str= (char*) malloc(sizeof(char)* MAXCHAR);
scanf("%s",str);
return str;}

int str_to_bool(char* expr){
int i=0;
if ((strcmp(expr, "true")==0) || (strcmp(expr, "1"))==0 )
i=1;
if ((strcmp(expr, "false")==0) || (strcmp(expr, "0"))==0 )
i=0;
return i;}

typedef struct {
	float value0;
	float value1;
	float value2;
	float value3;
} tutte_le_operazioniStruct;

char* stampa (char* stringa);
tutte_le_operazioniStruct tutte_le_operazioni (float input1, float input2);
float divisione (float input1, float input2);
float moltiplicazione (float input1, float input2);
void sottrazione (float input1, float input2, float *result);
void somma (float input1, float input2, float *result);

char* stampa (char* stringa){
char* s = (char*) malloc(MAXCHAR);
s = strncpy(s,"Ciao! ", MAXCHAR);
return str_concat(s,stringa);
}
tutte_le_operazioniStruct tutte_le_operazioni (float input1, float input2){
float somma_res = 0.0;
float sottrazione_res = 0.0;
somma(input1, input2, &somma_res);
sottrazione(input1, input2, &sottrazione_res);
tutte_le_operazioniStruct tutte_le_operazioniStruct;
tutte_le_operazioniStruct.value0 = somma_res;
tutte_le_operazioniStruct.value1 = sottrazione_res;
tutte_le_operazioniStruct.value2 = divisione(input1, input2);
tutte_le_operazioniStruct.value3 = moltiplicazione(input1, input2);
return tutte_le_operazioniStruct;
}
float divisione (float input1, float input2){
float result;
if(input2==0){
printf("Errore");
result=0.0;
}
result=input1/input2;
return result;
}
float moltiplicazione (float input1, float input2){
float result;
result=input1*input2;
return result;
}
void sottrazione (float input1, float input2, float *result){
*result=input1-input2;
}
void somma (float input1, float input2, float *result){
*result=input1+input2;
}
void main(int argc, char *argv[]){
bool flag = true;
float result;
float res1;
float res2;
float res3;
float res4;
char* operazione = (char*) malloc(MAXCHAR);
float input1;
float input2;
float answer;
while(flag==true) {
printf("Inserisci l'operazione da effettuare (somma, sottrazione, divisione, moltiplicazione, tutte_le_operazioni): ");
scanf("%s", operazione);
printf("Inserisci il primo input: ");
scanf("%f", &input1);
printf("Inserisci il secondo input: ");
scanf("%f", &input2);
printf("hai scelto l'operazione""%s ", operazione, "con gli argomenti""%f ", input1, " e ""%f ", input2, "\n");
if(strcmp(operazione,"somma")==0){
somma(input1, input2, &result);
}
else if(strcmp(operazione,"sottrazione")==0) {
sottrazione(input1, input2, &result);
}
else if(strcmp(operazione,"divisione")==0) {
result=divisione(input1, input2);
}
else if(strcmp(operazione,"moltiplicazione")==0) {
result=moltiplicazione(input1, input2);
}
else if(strcmp(operazione,"tutte_le_operazioni")==0) {
tutte_le_operazioniStruct tutte_le_operazioniStr16= tutte_le_operazioni(input1, input2);
res1= tutte_le_operazioniStr16.value0;
res2= tutte_le_operazioniStr16.value1;
res3= tutte_le_operazioniStr16.value2;
res4= tutte_le_operazioniStr16.value3;
}
else{
printf("Operazione non consentita", "\n");
}
if(strcmp(operazione,"tutte_le_operazioni")!=0){
printf("Il risultato e': ""%f ", result, "\n");
}
else{
printf("%s", stampa(str_concat(str_concat(str_concat(str_concat("i risultati delle 4 operazioni sono ",real_to_str(res1)),real_to_str(res2)),real_to_str(res3)),real_to_str(res3))), "\n");
}
printf("Vuoi continuare? (1 yes/0 no): ");
scanf("%f", &answer);
if(answer==1){
flag=true;
}
else{
flag=false;
}
}
}

