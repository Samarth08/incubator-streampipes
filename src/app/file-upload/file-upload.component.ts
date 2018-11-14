import { Component, OnInit } from '@angular/core';
import { FileRestService}  from './service/filerest.service';
import { MatSnackBar } from '@angular/material';

@Component({
  selector: 'sp-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

  selectedUploadFile: File;
  fileName;

  urls;

  constructor(
      private restService: FileRestService,
      public snackBar: MatSnackBar,) { }

  ngOnInit() {
      this.getURLS();
  }

  handleFileInput(files: any) {
      this.selectedUploadFile = files[0];
      this.fileName = this.selectedUploadFile.name;
  }

  upload() {
      if (this.selectedUploadFile !== undefined) {
          this.restService.upload(this.selectedUploadFile).subscribe(
              result => {
                this.getURLS();
              },
              error => {
                  this.openSnackBar('Error while uploading', 'Ok');
              },
          );
      }
  }

  getURLS() {
    this.restService.getURLS().subscribe(
        result => {
            this.urls = result;
        },
        error => {
            this.openSnackBar('Error while getting uploaded files', 'Ok');
        },
    );

  }

  delete(name: string) {
      this.restService.delete(name).subscribe(
          result => {
              this.openSnackBar('Deleted successful', 'Ok');
              this.getURLS();
          },
          error => {
              this.openSnackBar('Error while deleting file', 'Ok');
          },
      );
  }


  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top'
    });
  }

  copyText(val: string){
      let selBox = document.createElement('textarea');
      selBox.style.position = 'fixed';
      selBox.style.left = '0';
      selBox.style.top = '0';
      selBox.style.opacity = '0';
      selBox.value = val;
      document.body.appendChild(selBox);
      selBox.focus();
      selBox.select();
      document.execCommand('copy');
      document.body.removeChild(selBox);
      this.openSnackBar('Copied URL to Clipboard', 'Ok');
  }



}
