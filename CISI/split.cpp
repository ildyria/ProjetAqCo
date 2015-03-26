#include <string>
#include <iostream>
#include <fstream>


int main(int argc, char const *argv[])
{
	std::string str_line;
	std::ifstream myfile;
	std::ofstream tofiles;

	std::string filename = "NONE";
	myfile.open("CISI.ALLnettoye");
	while(!myfile.eof()) // To get you all the lines.
	{
		getline(myfile,str_line); // Saves the line in STRING.
		if(str_line.compare(0,3,".I ") == 0)
		{
			if(filename != "NONE")
			{
				tofiles.close();
			}
			getline(myfile,str_line); // Saves the line in STRING.
			filename = "files/";
			str_line.resize(str_line.size() -1);
			filename = filename + str_line + ".txt";
			std::cout << str_line << std::endl;
			tofiles.open(filename.c_str());
			tofiles << str_line << std::endl;
		}
		else
		{
			tofiles << str_line << std::endl;
		}
	}
	std::cout << std::endl;
	myfile.close();
	tofiles.close();

	return 0;
}