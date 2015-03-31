#include <string>
#include <iostream>
#include <fstream>


int main(int argc, char const *argv[])
{
	std::string str_line;
	std::string str_num;
	std::ifstream myfile;
	std::ofstream tofiles;

	std::string filename = "NONE";
	myfile.open("CISI.ALLnettoye");
	int number = 1;
	int previous = 0;
	while(!myfile.eof()) // To get you all the lines.
	{
		getline(myfile,str_line); // Saves the line in STRING.
		if(str_line.compare(0,3,".I ") == 0)
		{
			str_num = str_line.substr(3,std::string::npos);
			number = std::stoi(str_num,nullptr,0);
			if(filename != "NONE")
			{
				tofiles.close();
			}
			getline(myfile,str_line); // Saves the line in STRING.

			str_line.resize(str_line.size() -1);
			str_num.resize(str_num.size() -1);
			filename = "files/[" + str_num + "] " + str_line + ".txt";
			if(number != (previous + 1))
			{
				std::cout << "problem : " << number << std::endl;
			}
			previous = number;
			tofiles.open(filename.c_str());
			tofiles << str_line << std::endl;
			number++;
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