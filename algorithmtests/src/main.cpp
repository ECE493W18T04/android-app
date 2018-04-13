#include <stdio.h>
// #include "AutoBrightnessAlgorithm.h"
//
// AutoBrightnessAlg alg;
//
// int main(void)
// {
//   FILE *myfile;
//   float myvariable;
//   int i;
//
//   myfile=fopen("myfile.txt", "r");
//   while(!feof(myfile)) {
//       fscanf(myfile,"%d, %f\n", &i, &myvariable);
//       alg.addSample(myvariable);
//       printf("%d, %f, %d\n", i, myvariable, alg.getState());
//   }
//
//   fclose(myfile);
// }
#include "EnvironmentManager.h"

EnvironmentManager mgr;

int main(void)
{
  FILE *myfile;
  int myvariable;
  int i;

  myfile=fopen("myfile.txt", "r");
  while(!feof(myfile)) {
      fscanf(myfile,"%d, %d\n", &i, &myvariable);
      mgr.addSample(myvariable);
      printf("%d, %d, %d\n", i, myvariable, mgr.getBrightnessLimit());
  }

  fclose(myfile);
}
